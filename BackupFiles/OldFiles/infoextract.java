import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;



public class infoextract {
    private static ArrayList<Template> templates = new ArrayList<>();
    private static ArrayList<String> stories = new ArrayList<>();
    private static ArrayList<String> NOUNS = new ArrayList<>();
    private static ArrayList<String> POS = new ArrayList<>();
    private static ArrayList<String> WORDS = new ArrayList<>();
    public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException{
    	//parseInputStories("resources/test2.txt");
    	//generateTemplatesFromStories();
    	//createTemplatesFile("test2.txt");
    	//System.out.println(templates);
        parseInputStories(args[0]);

        generateTemplatesFromStories();

        createTemplatesFile(args[0]);
    	
        /*//This would ideally be the total contents of main
        * parseInputStories(args[0]);
        *
        * generateTemplatesFromStories();
        *
        * createTemplatesFile(args[0]);
        * */

        //Testing parse input stories
        /*parseInputStories("resources/test.txt");
        for(String s: stories){
            System.out.println(s);
        }*/

        //Testing output format
       /* Template temp = new Template();

        temp
                .setId("DEV-MUC3-0046")
                .setIncident("ATTACK")
                .addPerpIndiv("DEMOCRATIC PATRIOTIC COMMITTEES")
                .addPerpIndiv("OUR ORGANIZATION’S DISCIPLINARY TRIBUNAL")
                .addPerpOrg("ANTICOMMUNIST ACTION ALLIANCE")
                .addVictim("JORGE ARTURO REINA")
                .addVictim("JUAN ALMENDAREZ BONILLA")
                .addVictim("RAMON CUSTODIO LOPEZ")
                .addVictim("ANIBAL PUERTO")
                .addVictim("HECTOR HERNANDEZ");
        templates.add(temp);
        templates.add(temp);

        System.out.println(temp.toString());

        createTemplatesFile("news.txt"); */
    }

    /**Generates a file named < fileName >.templates which contains the templates
     * for each news story provided in < fileName >*/
    private static void createTemplatesFile(String fileName){
        try(PrintWriter out = new PrintWriter(fileName + ".templates")){
            for(Template temp: templates){
                out.println(temp.toString());
            }
        }catch (Exception e){
            System.out.println("Error: Failed to create: " + fileName + ".templates");}
    }

    /**Reads an input file containing stories separated by one of three different headers:
     * DEV-MUC3-XXXX, TST1-MUC3-XXXX, or TST2-MUC4-XXXX (where the X’s are digits)
     * Each story is stored separately from the others*/
    private static void parseInputStories(String fileName){
        String line;
        StringBuilder story = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("DEV-MUC") || line.startsWith("TST1-MUC3") || line.startsWith("TST2-MUC4")){
                    // The first pass will be empty, this check is to avoid adding an empty string to our stories
                    if(!story.toString().isEmpty()){
                        stories.add(story.toString());
                        story.setLength(0);
                    }
                }
                // Not sure if it's necessary to preserve the new lines
                story.append(line + "\n");
            }
            stories.add(story.toString());

        } catch (IOException ex) {
            System.out.println("Error: Could not read given input stories file: " + fileName + ".");
        }
    }

    /**Parses and creates a template for each story and stores it
     * @throws IOException 
     * @throws ClassNotFoundException 
     * @throws ClassCastException */
    private static void generateTemplatesFromStories() throws ClassCastException, ClassNotFoundException, IOException{
        Template template;
        for(String story : stories) {
        	findPOS(story);
        
        	findNouns(story);
        	
            template = new Template()
                    .setId(findStoryId(story))
                    .setIncident(findStoryIncident(story));
            for(int i=0;i<findStoryWeapon(story).size();i++) {
            		template.addWeapon(findStoryWeapon(story).get(i));
            		}
            for(int i=0;i<findStoryVictim(story).size();i++) {
        		template.addVictim(findStoryVictim(story).get(i));
        		}
            	
            	for(int i=0;i<findStoryTarget(story).size();i++) {
            		template.addTarget(findStoryTarget(story).get(i));
            		}
            		
            //The the logic for parsing the story and building the template should be here
            //Incident
            //Weapon
            //Perp Indiv
            //Perp Org
            //Target
            //Victim
            templates.add(template);
            NOUNS.clear();
            POS.clear();
            WORDS.clear();
        }
    }
    /**Used for retrieving the ID of a story. We take advantage of the fact that the first
     * line will always be formatted in a way where the ID is followed by a '(' */
    private static String findStoryId(String story){
        return story.substring(0, story.indexOf("(")).trim();
    }
    /**Used for determining the Incident type for a given story.
     * The possible Incident types are: ARSON, ATTACK, BOMBING, KIDNAPPING, ROBBERY
     * We check each word in the story and see which key words defined for each type occur most frequently*/
    private static String findStoryIncident(String story){
        Incident
                arson       = new Incident().setType("ARSON"),
                attack      = new Incident().setType("ATTACK"),
                bombing     = new Incident().setType("BOMBING"),
                kidnapping  = new Incident().setType("KIDNAPPING"),
                robbery     = new Incident().setType("ROBBERY");
        List<String>
                arsonKeyWords       = Arrays.asList(Constants.INCIDENT_ARSON),
                attackKeyWords      = Arrays.asList(Constants.INCIDENT_ATTACK),
                bombingKeyWords     = Arrays.asList(Constants.INCIDENT_BOMBING),
                kidnappingKeyWords  = Arrays.asList(Constants.INCIDENT_KIDNAPPING),
                robberyKeyWords     = Arrays.asList(Constants.INCIDENT_ROBBERY);

        String[] storySplitByWords = story.split("\\s+");

        for(String word:storySplitByWords){
            if (arsonKeyWords.contains(word.toLowerCase())) {
                arson.incrementOccurrence();
            } else if (attackKeyWords.contains(word.toLowerCase())) {
                attack.incrementOccurrence();
            } else if (bombingKeyWords.contains(word.toLowerCase())) {
                bombing.incrementOccurrence();
            } else if (kidnappingKeyWords.contains(word.toLowerCase())) {
                kidnapping.incrementOccurrence();
            } else if (robberyKeyWords.contains(word.toLowerCase())) {
                robbery.incrementOccurrence();
            }
        }

        List<Incident> incidents = new ArrayList<>();
        incidents.add(arson);
        incidents.add(attack);
        incidents.add(bombing);
        incidents.add(kidnapping);
        incidents.add(robbery);

        incidents.sort(Comparator.comparing(Incident::getOccurrence));

        // if after sorting, the last element is size 0, we found no keywords, return ATTACK as default
        if(incidents.get(incidents.size()-1).getOccurrence() == 0){
            return attack.getType();
        } else {
            return incidents.get(incidents.size()-1).getType();
        }
    }
    private static void findPOS(String story){
    	   Properties props = new Properties();
  	   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
  	   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  	   Annotation document = new Annotation(story);
  	   pipeline.annotate(document);
  	   MaxentTagger tagger = new MaxentTagger("resources/english-bidirectional-distsim.tagger"); //TODO when turning in, make sure we have the correct relative path
  	   String tagged = tagger.tagString(story);
  	   //System.out.println(tagged);
  	   String [] tokens=tagged.split(" ");
  	   for(int i=0;i<tokens.length;i++){
  		  // if(tokens[i].split("_")[1].equals("NNP") ||tokens[i].split("_")[1].equals("NNS")||tokens[i].split("_")[1].equals("NN") ||tokens[i].split("_")[1].equals("NNPS")) {
  			   POS.add(tokens[i].split("_")[0]+":"+tokens[i].split("_")[1]+":"+i);
  			   WORDS.add(tokens[i].split("_")[0]+":"+i);
  	   }
    }   
    private static void findNouns(String story){
	   for(int i=0;i<POS.size();i++){
		   if(POS.get(i).split(":")[1].equals("NNP") ||POS.get(i).split(":")[1].equals("NNS")||POS.get(i).split(":")[1].equals("NN") ||POS.get(i).split(":")[1].equals("NNPS")) {
			   NOUNS.add(POS.get(i));
		   }
	   }
    }
    
   private static ArrayList<String> findStoryWeapon(String story){
	   String weapon="";
	   HashMap<String,Integer> weapons=new HashMap<>();
	   ArrayList<String> weap=new ArrayList<String>();
	   
	   List<String>
       weaponsKeyWords       = Arrays.asList(Constants.WEAPONS);
	   for(int i=0;i<WORDS.size();i++){
		   int count=0;
           if (weaponsKeyWords.contains(WORDS.get(i).split(":")[0])) {
        	   		System.out.println(WORDS.get(i));
        	   		count++;
        	   		weapons.put(NounPhrase(WORDS.get(i)), count);
           }
       }
	   System.out.println(weapons);
	   if(!weapons.isEmpty()) {
		   for(String key:weapons.keySet()) {
			   if(weapons.get(key)>0) {
				   weap.add(key);
			   }
		   }
	   }
	   return weap;
   }
   private static ArrayList<String> findStoryTarget(String story) {
	   HashMap<String,Integer> targets=new HashMap<String,Integer>();
	   ArrayList<String> tar=new ArrayList<String>();
	   List<String> keyWords=Arrays.asList(Constants.TARGET);
	   for(int i=1;i<WORDS.size();i++) {
		   int count=0;
		   if(keyWords.contains(WORDS.get(i).split(":")[0])) {
			   count++;
			   targets.put(WORDS.get(i)+":"+i,count);
		   }
	   }
	   for(String key:targets.keySet()) {
		   if(targets.get(key)==1) {
			   String g=NounPhrase(key);
			   tar.add(g);
			   
		   }
		  
	   }
	   
	   return tar;
   }
   private static String NounPhrase(String word){
	   String x="";
	   int first=Integer.valueOf(word.split(":")[1])+2;
	   int last=Integer.valueOf(word.split(":")[1])-2;
  			for(int j=Integer.valueOf(word.split(":")[1]);j<Math.min(Integer.valueOf(word.split(":")[1])+2,POS.size());j++) {
  				if(!POS.get(j).split(":")[1].equals("NNP") && !POS.get(j).split(":")[1].equals("NNS")&& !POS.get(j).split(":")[1].equals("NN") &&!POS.get(j).split(":")[1].equals("NNPS")) {
  					first=Math.min(j,first);
  				  }
  			  }
  			for(int k=Integer.valueOf(word.split(":")[1])-1;k<=Math.max(Integer.valueOf(word.split(":")[1])-2,0);k--) {
  				last=Integer.valueOf(word.split(":")[1])+1;
  				if(!POS.get(k).split(":")[1].equals("NNP") && !POS.get(k).split(":")[1].equals("NNS")&& !POS.get(k).split(":")[1].equals("NN") && !POS.get(k).split(":")[1].equals("NNPS")) {
  					last=Math.max(k, last);
  					x=POS.get(k).split(":")[0]+" "+x;
    			  }
  			}
  			
  			for(int p=Integer.valueOf(word.split(":")[1])-1;p<=last;p--) {
				x=POS.get(p).split(":")[0]+" "+x;
			}
  			for(int o=Integer.valueOf(word.split(":")[1])-1;o<first;o++) {
				x=x+" "+POS.get(o).split(":")[0];
			}
	return x;
	   
   }
   private static ArrayList<String> findStoryVictim(String story) throws ClassCastException, ClassNotFoundException, IOException{
	   String victim="";
	   ArrayList<String> vic=new ArrayList<String>();
	   String modelPath = DependencyParser.DEFAULT_MODEL;
	  
	   String taggerPath = "resources/english-left3words-distsim.tagger"; // TODO make sure we have the correct relative path when turing in
	   MaxentTagger tagger = new MaxentTagger(taggerPath);
	    DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);
	    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(story));
	    List<String>
        arsonKeyWords       = Arrays.asList(Constants.INCIDENT_ARSON),
        attackKeyWords      = Arrays.asList(Constants.INCIDENT_ATTACK),
        bombingKeyWords     = Arrays.asList(Constants.INCIDENT_BOMBING),
        kidnappingKeyWords  = Arrays.asList(Constants.INCIDENT_KIDNAPPING),
        robberyKeyWords     = Arrays.asList(Constants.INCIDENT_ROBBERY);
	    for (List<HasWord> sentence : tokenizer) {
		      List<TaggedWord> tagged = tagger.tagSentence(sentence);
		      GrammaticalStructure gs= parser.predict(tagged);
		      List<TypedDependency> x = gs.typedDependenciesEnhancedPlusPlus();
		      for(int i=0;i<x.size();i++) {
		    	  	if(x.get(i).reln().toString().equals("amod") || x.get(i).reln().toString().equals("nmod:of")) {
		    	  		String [] y=x.get(i).toString().split(",");
		    	  		String word1=y[0].split("\\(")[1].split("-")[0];
		    	  		String word2=y[1].split("\\)")[0].split("-")[0].replaceAll(" ", "");
		    	  		
		    	  		int num1=Integer.valueOf(y[0].split("\\(")[1].split("-")[1]);
		    	  		int num2=Integer.valueOf(y[1].split("\\)")[0].split("-")[1]);
		    	  		if(num1>num2 && (arsonKeyWords.contains(word2.toLowerCase()) || attackKeyWords.contains(word2.toLowerCase())||bombingKeyWords.contains(word2.toLowerCase())||kidnappingKeyWords.contains(word2.toLowerCase())||robberyKeyWords.contains(word2.toLowerCase()))) {
		    	  			if(!arsonKeyWords.contains(word1.toLowerCase()) && !attackKeyWords.contains(word1.toLowerCase()) && !bombingKeyWords.contains(word1.toLowerCase()) || !kidnappingKeyWords.contains(word1.toLowerCase())||!robberyKeyWords.contains(word1.toLowerCase()))
		    	  			vic.add(word1);
		    	  		}
		    	  	}	
		      }    
	    }  
	  
	   return vic;
   }
    /**This class makes it easier to get the type with the most frequently occurring key words*/
    private static class Incident{
        private Incident(){}
        private String _type;
        private int _occurrence = 0;

        private Incident setType(String type){
            _type = type;
            return this;
        }
        private String getType(){
            return _type;
        }
        private void incrementOccurrence(){
            _occurrence++;
        }
        private int getOccurrence(){
            return _occurrence;
        }
    }

}
