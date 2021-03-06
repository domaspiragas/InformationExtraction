import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

/*
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
*/
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class infoextract {
    private static ArrayList<Template> templates = new ArrayList<>();
    private static ArrayList<String> stories = new ArrayList<>();
//    private static ArrayList<String> NOUNS = new ArrayList<>();
 //   private static ArrayList<String> POS = new ArrayList<>();
 //   private static ArrayList<String> WORDS = new ArrayList<>();
    private static ArrayList<String> Sents = new ArrayList<>();
//	private static Parse[] topParses;
    public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException{
    	parseInputStories("TST2-MUC4-0025");
    	generateTemplatesFromStories();
    	System.out.println(templates);
    	
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
      //  	findPOS(story);
     //   	findNouns(story);
        //	Sentences(story);
            template = new Template()
                    .setId(findStoryId(story))
                    .setIncident(findStoryIncident(story));
            
           
           // 		template.addWeapon(findStoryWeapon(story).get(i));
            //		}
            
            
            ArrayList<String> y=cleanData(findStoryVictim(story));
            for(int i=0;i<y.size();i++) {
            		
            			template.addVictim(y.get(i));
        		}
            
            ArrayList<String> y1=cleanData(findStoryTarget(story));
         //   System.out.println(y1);
            for(int i=0;i<y1.size();i++) {
            		
            			template.addTarget(y1.get(i));
            		//}
        		}
            ArrayList<String> y2=cleanData(findStoryInd(story));
            
            for(int i=0;i<y2.size();i++) {
            		
            			template.addPerpIndiv(y2.get(i));
            		
        		}
            
            ArrayList<String> y3=cleanData(findStoryOrg(story));
            
            for(int i=0;i<y3.size();i++) {
            		
            			template.addPerpOrg(y3.get(i));
            		
        		}
           // ArrayList<String> weap=findStoryWeapon(story);
             for(int i=0;i<findStoryWeapon(story).size();i++) {
            	 	template.addWeapon(findStoryWeapon(story).get(i).toUpperCase());
             }
             	
            //The the logic for parsing the story and building the template should be here
            //Incident
            //Weapon
            //Perp Indiv
            //Perp Org
            //Target
            //Victim
            templates.add(template);
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
 /*   private static void findPOS(String story){
    	   Properties props = new Properties();
  	   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
  	   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  	   Annotation document = new Annotation(story);
  	   pipeline.annotate(document);
  	   MaxentTagger tagger = new MaxentTagger("/Users/maryambarouti/Desktop/ProjectNLP/Project/stanford-postagger-2017-06-09/models/english-bidirectional-distsim.tagger");
  	   String tagged = tagger.tagString(story.toLowerCase());
  	   //System.out.println(tagged);
  	   String [] tokens=tagged.split(" ");
  	   
  	   for(int i=0;i<tokens.length;i++){
  		  // if(tokens[i].split("_")[1].equals("NNP") ||tokens[i].split("_")[1].equals("NNS")||tokens[i].split("_")[1].equals("NN") ||tokens[i].split("_")[1].equals("NNPS")) {
  			   POS.add(tokens[i].split("_")[0]+":"+tokens[i].split("_")[1]+":"+i);
  			   WORDS.add(tokens[i].split("_")[0]+":"+i);
  	   }
  	   //System.out.println(POS);
    }   
    private static void findNouns(String story){
	   for(int i=0;i<POS.size();i++){
		   if(POS.get(i).split(":")[1].equals("NNP") ||POS.get(i).split(":")[1].equals("NNS")||POS.get(i).split(":")[1].equals("NN") ||POS.get(i).split(":")[1].equals("NNPS")) {
			   NOUNS.add(POS.get(i));
		   }
	   }
    }
    */
 
   private static ArrayList<ArrayList<String>> findStoryTarget(String story) throws IOException {
	   ArrayList<ArrayList<String>> res=new ArrayList<ArrayList<String>>();
		String [] after=patterns.pattern_target_after;
		String [] before=patterns.pattern_target_before;
		for(String s:Sents) {
			for(String h:after) {
				//System.out.println(s.toLowerCase().contains(h));
				if(s.toLowerCase().contains(h))
					res.add(findNounPhraseTarget(s,h,"after"));
			}
		for(String t:before) {
			//System.out.println(s.toLowerCase().contains(t));
			if(s.toLowerCase().contains(t)) {
			res.add(findNounPhraseTarget(s,t,"before"));}
		}
		
	   }
		return res;
		}
   private static ArrayList<ArrayList<String>> findStoryOrg(String story) throws IOException {
	   ArrayList<ArrayList<String>> res=new ArrayList<ArrayList<String>>();
		String [] after=patterns.pattern_org_after;
		String [] before=patterns.pattern_org_before;
		for(String s:Sents) {
			for(String h:after) {
				//System.out.println(s.toLowerCase().contains(h));
				if(s.toLowerCase().contains(h))
					res.add(findNounPhraseOrg(s,h,"after"));
			}
		for(String t:before) {
			//System.out.println(s.toLowerCase().contains(t));
			if(s.toLowerCase().contains(t))
			res.add(findNounPhraseOrg(s,t,"before"));
		}
		
	   }
		return res;
		}
   private static ArrayList<String> cleanData(ArrayList<ArrayList<String>> uncleaned){
	   ArrayList<String> cleaned=new ArrayList<String>();
	   for(int i=0;i<uncleaned.size();i++) {
		   for(int j=0;j<uncleaned.get(i).size();j++) {
			   String x=uncleaned.get(i).get(j);
			   if(!cleaned.contains(x) && x!=null) {
				 if(x.contains(" AND ")) {
					 String []words=x.split(" AND ");
					 if(!cleaned.contains(removeExtraBef(removeExtraAf(words[0]))) && words[0]!=""){
					 cleaned.add(removeExtraBef(removeExtraAf(words[0])));}
					 if(!cleaned.contains(removeExtraBef(removeExtraAf(words[1]))) && words[1]!=""){
						// System.out.println(words[1]);
						 cleaned.add(removeExtraBef(removeExtraAf(words[1])));}
				 }
				 if(!cleaned.contains(removeExtraBef(removeExtraAf(x))))
					 cleaned.add(removeExtraBef(removeExtraAf(x))); 
			   }
		   }
	   }
	   return cleaned;
   }
  private static String removeExtraBef(String x) {
	  String newS="";
	  ArrayList<String> pat=new ArrayList<String>();
	for(String s:Constants.Extras_before) {
		if(x.contains(s) && x.indexOf(s)==0) {
			pat.add(s);
			if(pat.size()>0) {
				newS=x.replace(s+" ", "");}
				}
			if(pat.size()==0) {
				newS=x;
			}
	}
	return newS;
}

private static String removeExtraAf(String x) {
	String st="";
	int len=x.toCharArray().length;
	ArrayList<String> pat=new ArrayList<String>();
	for(String h:Constants.Extras_after) {
		if(x.contains(h) && x.indexOf(h)==len-1){
			pat.add(h);
			if(pat.size()>0) {
			st=x.replace(h,"");}}
		if(pat.size()==0) {
			st=x;
		}
	}
	return st;
}

/* private static String NounPhrase(String word){
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
	   
   }*/
  private static void Sentences(String story) throws IOException {
	  InputStream inputStream = new FileInputStream("/Users/maryambarouti/Downloads/opnenlp-models/en-sent.bin"); 
      SentenceModel model = new SentenceModel(inputStream); 
       
      //Instantiating the SentenceDetectorME class 
      SentenceDetectorME detector = new SentenceDetectorME(model);  
    
      //Detecting the sentence
      String sentences[] = detector.sentDetect(story); 
    
      //Printing the sentences 
      for(String sent : sentences)        
         Sents.add(sent);  
   } 
  
   
	
   private static ArrayList<String> findNounPhraseVictim(String story,String pattern,String string) throws IOException {
	    ArrayList<String> arr=new ArrayList<String>();
	    InputStream is = new FileInputStream("/Users/maryambarouti/Downloads/opnenlp-models/en-parser-chunking.bin");
		ParserModel model = new ParserModel(is);
		int len=pattern.split(" ").length;
		opennlp.tools.parser.Parser parser = ParserFactory.create(model);
		Parse [] topParses= ParserTool.parseLine(story.toLowerCase(), parser, 1);
	    if(string.equals("after")) {
			for (Parse p : topParses) {
				for(int i=0;i<p.getTagNodes().length-len+1;i++) {
					String t=""; // generate chunks
					for(int l=0;l<len;l++) { if(l==0) {
						t=t+p.getTagNodes()[i];
					}
					else{t=t+" "+p.getTagNodes()[i+l];}}
					//System.out.println("after::"+t);
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i+len].getParent()!=null && p.getTagNodes()[i+len]!=null ) {
							if(p.getTagNodes()[i+len].getParent().getType().equals("NP")) {
								//System.out.println(p.getTagNodes()[i+len].getParent());
								String [] x= Constants.TARGET;
								ArrayList<Boolean> exists=new ArrayList<>();
								for(String h:x) {
									exists.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase().contains(h));
										
									//	arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());}
								//arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
								}
								if(!exists.contains(true)) {
									arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
								}
							}
						}
					}
				}
			}
	   }
	
	   if(string.equals("before")) {
		   for (Parse p : topParses) {
				for(int i=1;i<p.getTagNodes().length-len+1;i++) {
					String t="";
					for(int l=0;l<len;l++) { 
						if(l==0) {
							t=t+p.getTagNodes()[i];
						}
						else{
							t=t+" "+p.getTagNodes()[i+l];
							}
						}
					//System.out.println("before::"+t);
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i-1].getParent()!=null && p.getTagNodes()[i-1]!=null ) {
							if(p.getTagNodes()[i-1].getParent().getType().equals("NP")) {
						//		System.out.println(p.getTagNodes()[i-1].getParent());
						//		System.out.println(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
								String [] x= Constants.TARGET;
								ArrayList<Boolean> exists=new ArrayList<>();
								for(String h:x) {
									exists.add((p.getTagNodes()[i-1].getParent().toString().toUpperCase().contains(h)));
								}
								if(!exists.contains(true)) {
									arr.add(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
								}
							}
						}
					}
				}
			}		
	   }
	   
	return arr;   
}
   private static ArrayList<String> findNounPhraseTarget(String story,String pattern,String string) throws IOException {
	   ArrayList<String> arr=new ArrayList<String>();
	    InputStream is = new FileInputStream("/Users/maryambarouti/Downloads/opnenlp-models/en-parser-chunking.bin");
		ParserModel model = new ParserModel(is);
		int len=pattern.split(" ").length;
		opennlp.tools.parser.Parser parser = ParserFactory.create(model);
		Parse[]topParses= ParserTool.parseLine(story.toLowerCase(), parser, 1);
	  // int len=pattern.split(" ").length;
	 //  ArrayList<String> arr=new ArrayList<String>();
	    if(string.equals("after")) {
			for (Parse p : topParses) {
				for(int i=0;i<p.getTagNodes().length-len;i++) {
					String t=""; // generate chunks
					for(int l=0;l<len;l++) { 
						if(l==0) {
						t=t+p.getTagNodes()[i];
					}
					else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i+len].getParent()!=null && p.getTagNodes()[i+len]!=null ) {
							if(p.getTagNodes()[i+len].getParent().getType().equals("NP")) {
								String [] x= Constants.TARGET;
								for(String h:x) {
									if(p.getTagNodes()[i+len].getParent().toString().toUpperCase().contains(h)) {
										arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());}
								}
							}
						}
					}
				}
			}
	   }
	   if(string.equals("before")) {
		   for (Parse p : topParses) {
				for(int i=1;i<p.getTagNodes().length-len;i++) {
					String t="";
					for(int l=0;l<len;l++) { 
						if(l==0) {
							t=t+p.getTagNodes()[i];
						}
						else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i-1].getParent()!=null && p.getTagNodes()[i-1]!=null ) {
							if(p.getTagNodes()[i-1].getParent().getType().equals("NP")) {
								String [] x= Constants.TARGET;
								for(String h:x) {
									if(p.getTagNodes()[i-1].getParent().toString().toUpperCase().contains(h)) {
										arr.add(p.getTagNodes()[i-1].getParent().toString().toUpperCase());}
								}
							}
						}
					}
				}
			}		
	   }
	return arr;   
   }
   private static ArrayList<String> findNounPhraseOrg(String story,String pattern,String string) throws IOException {
	    ArrayList<String> arr=new ArrayList<String>();
	    InputStream is = new FileInputStream("/Users/maryambarouti/Downloads/opnenlp-models/en-parser-chunking.bin");
		ParserModel model = new ParserModel(is);
		int len=pattern.split(" ").length;
		opennlp.tools.parser.Parser parser = ParserFactory.create(model);
		Parse [] topParses= ParserTool.parseLine(story.toLowerCase(), parser, 1);
	    if(string.equals("after")) {
			for (Parse p : topParses) {
				for(int i=0;i<p.getTagNodes().length-len+1;i++) {
					String t=""; // generate chunks
					for(int l=0;l<len;l++) { if(l==0) {
						t=t+p.getTagNodes()[i];
					}
					else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i+len].getParent()!=null && p.getTagNodes()[i+len]!=null ) {
							if(p.getTagNodes()[i+len].getParent().getType().equals("NP")) {
								String [] x= Constants.org_keyword;
								//System.out.println(x.length);
								for(String h:x) {
								//	System.out.println(h);
									if(p.getTagNodes()[i+len].getParent().toString().contains(h))
										arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
								}
							}
						}
					}
				}
			}
	   }
	   if(string.equals("before")) {
		   for (Parse p : topParses) {
				for(int i=1;i<p.getTagNodes().length-len+1;i++) {
					String t="";
					for(int l=0;l<len;l++) { 
						if(l==0) {
							t=t+p.getTagNodes()[i];
						}
						else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i-1].getParent()!=null && p.getTagNodes()[i-1]!=null ) {
							//System.out.println("+");
							if(p.getTagNodes()[i-1].getParent().getType().equals("NP")) {
								String [] x= Constants.org_keyword;
							//	System.out.println(x.length);
								for(String h:x) {
									//System.out.println(h);
									if(p.getTagNodes()[i-1].getParent().toString().contains(h))
										arr.add(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
								}
							}
						}
					}
				}
			}		
	   }
	   
	return arr;   
}
   private static ArrayList<String> findNounPhraseInd(String story,String pattern,String string) throws IOException {
	   ArrayList<String> arr=new ArrayList<String>();
	    InputStream is = new FileInputStream("/Users/maryambarouti/Downloads/opnenlp-models/en-parser-chunking.bin");
		ParserModel model = new ParserModel(is);
		int len=pattern.split(" ").length;
		opennlp.tools.parser.Parser parser = ParserFactory.create(model);
		Parse[]topParses= ParserTool.parseLine(story.toLowerCase(), parser, 1);
	  // int len=pattern.split(" ").length;
	 //  ArrayList<String> arr=new ArrayList<String>();
	    if(string.equals("after")) {
			for (Parse p : topParses) {
				for(int i=0;i<p.getTagNodes().length-len;i++) {
					String t=""; // generate chunks
					for(int l=0;l<len;l++) { 
						if(l==0) {
						t=t+p.getTagNodes()[i];
					}
					else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i+len].getParent()!=null && p.getTagNodes()[i+len]!=null ) {
							if(p.getTagNodes()[i+len].getParent().getType().equals("NP")) {
							//	System.out.println(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
								arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
							}
						}
					}
				}
			}
	   }
	
	   if(string.equals("before")) {
		   for (Parse p : topParses) {
				for(int i=1;i<p.getTagNodes().length-len;i++) {
					String t="";
					for(int l=0;l<len;l++) { 
						if(l==0) {
							t=t+p.getTagNodes()[i];
						}
						else{t=t+" "+p.getTagNodes()[i+l];}}
					if(t.contains(pattern)) {
						if(p.getTagNodes()[i-1].getParent()!=null && p.getTagNodes()[i-1]!=null ) {
							if(p.getTagNodes()[i-1].getParent().getType().equals("NP")) {
							//	System.out.println(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
								arr.add(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
							}
						}
					}
				}
			}		
	   }
	return arr;   
}
   private static ArrayList<ArrayList<String>> findStoryInd(String story) throws IOException {
		Sentences(story);
		ArrayList<ArrayList<String>> res=new ArrayList<ArrayList<String>>();
		String [] after=patterns.pattern_Ind_after;
		String [] before=patterns.pattern_Ind_before;
		for(String s:Sents) {
			for(String h:after) {
			//	System.out.println(s.toLowerCase().contains(h));
				if(s.toLowerCase().contains(h))
					res.add(findNounPhraseInd(s,h,"after"));
			}
		for(String t:before) {
		//	System.out.println(s.toLowerCase().contains(t));
			if(s.toLowerCase().contains(t))
			res.add(findNounPhraseInd(s,t,"before"));
		}
		
	   }
		return res;
		}
private static ArrayList<ArrayList<String>> findStoryVictim(String story) throws IOException {
	Sentences(story);
	ArrayList<ArrayList<String>> res=new ArrayList<ArrayList<String>>();
	String [] after=patterns.pattern_Victim_after;
	String [] before=patterns.pattern_Victim_before;
	for(String s:Sents) {
		for(String h:after) {
			if(s.toLowerCase().contains(h))
				res.add(findNounPhraseVictim(s,h,"after"));
		}
	for(String t:before) {
	//	System.out.println(s.toLowerCase().contains(t));
		if(s.toLowerCase().contains(t))
			res.add(findNounPhraseVictim(s,t,"before"));
	}
   }
	return res;
	}

private static ArrayList<String> findStoryWeapon(String story) throws IOException{
	 
	   HashMap<String,Integer> weapons=new HashMap<>();
	   ArrayList<String> weap=new ArrayList<String>();
	   List<String> weaponsKeyWords= Arrays.asList(Constants.WEAPONS);
	   //System.out.println(weaponsKeyWords);
	 //  System.out.println(Sents);
	   for(String h:Sents){
		   
		   for (String x:weaponsKeyWords) {
     	   		int count=0;
     	   		if(h.toLowerCase().contains(x)) {
     	   			count++;
     	   			weapons.put(x, count);
        		}
    		}
        }
	   if(!weapons.isEmpty()) {
		   for(String key:weapons.keySet()) {
			   if(weapons.get(key)>0) {
				   weap.add(key);
			   }
		   }
	   }
	   return weap;
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
