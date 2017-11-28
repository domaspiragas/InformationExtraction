import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.io.FileInputStream;
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
    private static ArrayList<String> Sents = new ArrayList<>();

    public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {

        //This would ideally be the total contents of main
         parseInputStories(args[0]);

         generateTemplatesFromStories();

         createTemplatesFile(args[0]);

    }

    /**
     * Generates a file named < fileName >.templates which contains the templates
     * for each news story provided in < fileName >
     */
    private static void createTemplatesFile(String fileName) {
        try (PrintWriter out = new PrintWriter(fileName + ".templates")) {
            for (Template temp : templates) {
                out.println(temp.toString());
            }
        } catch (Exception e) {
            System.out.println("Error: Failed to create: " + fileName + ".templates");
        }
    }

    /**
     * Reads an input file containing stories separated by one of three different headers:
     * DEV-MUC3-XXXX, TST1-MUC3-XXXX, or TST2-MUC4-XXXX (where the X’s are digits)
     * Each story is stored separately from the others
     */
    private static void parseInputStories(String fileName) {
        String line;
        StringBuilder story = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("DEV-MUC") || line.startsWith("TST1-MUC3") || line.startsWith("TST2-MUC4")) {
                    // The first pass will be empty, this check is to avoid adding an empty string to our stories
                    if (!story.toString().isEmpty()) {
                        stories.add(story.toString());
                        story.setLength(0);
                    }
                }
                story.append(line + "\n");
            }
            stories.add(story.toString());

        } catch (IOException ex) {
            System.out.println("Error: Could not read given input stories file: " + fileName + ".");
        }
    }

    /**
     * Parses and creates a template for each story and stores it
     * {ID, Incident, Weapon, Perp Indiv, Perp Org, Target, Victim}
     */
    private static void generateTemplatesFromStories() throws ClassCastException, ClassNotFoundException, IOException {
        Template template;
        for (String story : stories) {
            Sentences(story);
            template = new Template()
                    .setId(findStoryId(story)) //ID
                    .setIncident(findStoryIncident(story)); // Incident

            //Weapon
            for(int i=0;i<findStoryWeapon().size();i++) {
                template.addWeapon(findStoryWeapon().get(i).toUpperCase());
            }

            //Perp Indiv
            ArrayList<String> y2 = cleanData(findCategoryResult(patterns.pattern_Ind_after, patterns.pattern_Ind_before,
                    null, false));
            for (int i = 0; i < y2.size(); i++) {

                template.addPerpIndiv(y2.get(i));
            }

            /*Perp Org Goes Here*/
            ArrayList<String> y3 = cleanData(findCategoryResult(patterns.pattern_org_after, patterns.pattern_org_before,
                    Constants.org_keyword, false));
            for (int i = 0; i < y3.size(); i++) {

                template.addPerpOrg(y3.get(i));
            }

            //Target
            ArrayList<String> y1 = cleanData(findCategoryResult(patterns.pattern_target_after, patterns.pattern_target_before,
                    Constants.TARGET, false));
            for (int i = 0; i < y1.size(); i++) {

                template.addTarget(y1.get(i));
            }

            //Victim
            ArrayList<String> y = cleanData(findCategoryResult(patterns.pattern_Victim_after, patterns.pattern_Victim_before,
                    Constants.TARGET, true));
            for (int i = 0; i < y.size(); i++) {

                template.addVictim(y.get(i));
            }

            templates.add(template);
            Sents.clear(); // Clear so next story only contains its own sentences
        }
    }

    /**
     * Used for retrieving the ID of a story. We take advantage of the fact that the first
     * line will always be formatted in a way where the ID is followed by a '('
     */
    private static String findStoryId(String story) {
        return story.substring(0, story.indexOf("(")).trim();
    }

    /**
     * Used for determining the Incident type for a given story.
     * The possible Incident types are: ARSON, ATTACK, BOMBING, KIDNAPPING, ROBBERY
     * We check each word in the story and see which key words defined for each type occur most frequently
     */
    private static String findStoryIncident(String story) {
        Incident
                arson = new Incident().setType("ARSON"),
                attack = new Incident().setType("ATTACK"),
                bombing = new Incident().setType("BOMBING"),
                kidnapping = new Incident().setType("KIDNAPPING"),
                robbery = new Incident().setType("ROBBERY");
        List<String>
                arsonKeyWords = Arrays.asList(Constants.INCIDENT_ARSON),
                attackKeyWords = Arrays.asList(Constants.INCIDENT_ATTACK),
                bombingKeyWords = Arrays.asList(Constants.INCIDENT_BOMBING),
                kidnappingKeyWords = Arrays.asList(Constants.INCIDENT_KIDNAPPING),
                robberyKeyWords = Arrays.asList(Constants.INCIDENT_ROBBERY);

        String[] storySplitByWords = story.split("\\s+");

        for (String word : storySplitByWords) {
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
        if (incidents.get(incidents.size() - 1).getOccurrence() == 0) {
            return attack.getType();
        } else {
            return incidents.get(incidents.size() - 1).getType();
        }
    }

    private static ArrayList<String> cleanData(ArrayList<ArrayList<String>> uncleaned) {
        ArrayList<String> cleaned = new ArrayList<String>();
        for (int i = 0; i < uncleaned.size(); i++) {
            for (int j = 0; j < uncleaned.get(i).size(); j++) {
                String x = uncleaned.get(i).get(j);
                if (!cleaned.contains(x) && x != null) {
                    if (x.contains(" AND ")) {
                        String[] words = x.split(" AND ");
                        if (!cleaned.contains(removeExtraBef(removeExtraAf(words[0])))) {
                            cleaned.add(removeExtraBef(removeExtraAf(words[0])));
                        }
                        if (!cleaned.contains(removeExtraBef(removeExtraAf(words[1])))) {
                            cleaned.add(removeExtraBef(removeExtraAf(words[1])));
                        }
                    }
                    if (!cleaned.contains(removeExtraBef(removeExtraAf(x))))
                        cleaned.add(removeExtraBef(removeExtraAf(x)));
                }
            }
        }
        return cleaned;
    }

    private static String removeExtraBef(String x) {
        String newS = "";
        ArrayList<String> pat = new ArrayList<>();
        for (String s : Constants.Extras_before) {
            if (x.contains(s) && x.indexOf(s) == 0) {
                pat.add(s);
                if (pat.size() > 0) {
                    newS = x.replace(s + " ", "");
                }
            }
            if (pat.size() == 0) {
                newS = x;
            }
        }
        return newS;
    }

    private static String removeExtraAf(String x) {
        String st = "";
        int len = x.toCharArray().length;
        ArrayList<String> pat = new ArrayList<>();
        for (String h : Constants.Extras_after) {
            if (x.contains(h) && x.indexOf(h) == len - 1) {
                pat.add(h);
                if (pat.size() > 0) {
                    st = x.replace(h, "");
                }
            }
            if (pat.size() == 0) {
                st = x;
            }
        }
        return st;
    }

    private static void Sentences(String story) throws IOException {
        InputStream inputStream = new FileInputStream("en-sent.bin");
        SentenceModel model = new SentenceModel(inputStream);

        //Instantiating the SentenceDetectorME class
        SentenceDetectorME detector = new SentenceDetectorME(model);

        //Detecting the sentence
        String sentences[] = detector.sentDetect(story);

        //Printing the sentences
        Sents.addAll(Arrays.asList(sentences));
    }

    private static ArrayList<String> findNounPhrase(String story, String pattern, String string, String[] constants, Boolean victim) throws IOException {
        ArrayList<String> arr = new ArrayList<>();
        InputStream is = new FileInputStream("en-parser-chunking.bin");
        ParserModel model = new ParserModel(is);
        int len = pattern.split(" ").length;
        opennlp.tools.parser.Parser parser = ParserFactory.create(model);
        Parse[] topParses = ParserTool.parseLine(story.toLowerCase(), parser, 1);
        StringBuilder t = new StringBuilder();
        if (string.equals("after")) {
            for (Parse p : topParses) {
                for (int i = 0; i < p.getTagNodes().length - len; i++) {
                    t.setLength(0); // generate chunks
                    for (int l = 0; l < len; l++) {
                        t.append(p.getTagNodes()[i + l]).append(" ");
                    }
                    if (t.toString().trim().contains(pattern)) {
                        if (p.getTagNodes()[i + len].getParent() != null && p.getTagNodes()[i + len] != null) {
                            if (p.getTagNodes()[i + len].getParent().getType().equals("NP")) {
                                if (constants != null) {
                                    if(!victim) {
                                        for (String h : constants) {
                                            if (p.getTagNodes()[i + len].getParent().toString().toUpperCase().contains(h)) {
                                                arr.add(p.getTagNodes()[i + len].getParent().toString().toUpperCase());
                                            }
                                        }
                                    }else{
                                        ArrayList<Boolean> exists=new ArrayList<>();
                                        for(String h:constants) {
                                            exists.add(p.getTagNodes()[i + len].getParent().toString().toUpperCase().contains(h));
                                        }
                                        if(!exists.contains(true)) {
                                            arr.add(p.getTagNodes()[i+len].getParent().toString().toUpperCase());
                                        }
                                    }
                                } else {
                                    arr.add(p.getTagNodes()[i + len].getParent().toString().toUpperCase());
                                }
                            }
                        }
                    }
                }
            }
        } else if (string.equals("before")) {
            for (Parse p : topParses) {
                for (int i = 0; i < p.getTagNodes().length - len; i++) {
                    t.setLength(0);
                    for (int l = 0; l < len; l++) {
                        t.append(p.getTagNodes()[i + l]).append(" ");
                    }
                    if (t.toString().trim().contains(pattern)) {
                        if (p.getTagNodes()[i - 1].getParent() != null && p.getTagNodes()[i - 1] != null) {
                            if (p.getTagNodes()[i - 1].getParent().getType().equals("NP")) {
                                if (constants != null) {
                                    if(!victim) {
                                        for (String h : constants) {
                                            if (p.getTagNodes()[i - 1].getParent().toString().toUpperCase().contains(h)) {
                                                arr.add(p.getTagNodes()[i - 1].getParent().toString().toUpperCase());
                                            }
                                        }
                                    }else{
                                        ArrayList<Boolean> exists=new ArrayList<>();
                                        for(String h:constants) {
                                            exists.add((p.getTagNodes()[i-1].getParent().toString().toUpperCase().contains(h)));
                                        }
                                        if(!exists.contains(true)) {
                                            arr.add(p.getTagNodes()[i-1].getParent().toString().toUpperCase());
                                        }
                                    }
                                } else {
                                    arr.add(p.getTagNodes()[i - 1].getParent().toString().toUpperCase());
                                }
                            }
                        }
                    }
                }
            }
        }

        return arr;
    }

    private static ArrayList<ArrayList<String>> findCategoryResult(String[] after, String[] before, String[] constants, Boolean victim) throws IOException {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for (String s : Sents) {
            for (String h : after) {
                if (s.toLowerCase().contains(h)) {
                    res.add(findNounPhrase(s, h, "after", constants, victim));
                }
            }
            for (String t : before) {
                if (s.toLowerCase().contains(t)) {
                    res.add(findNounPhrase(s, t, "before", constants, victim));
                }
            }
        }
        return res;
    }

    private static ArrayList<String> findStoryWeapon() throws IOException{

        HashMap<String,Integer> weapons=new HashMap<>();
        ArrayList<String> weap=new ArrayList<>();
        List<String> weaponsKeyWords= Arrays.asList(Constants.WEAPONS);

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

    /**
     * This class makes it easier to get the type with the most frequently occurring key words
     */
    private static class Incident {
        private Incident() {
        }

        private String _type;
        private int _occurrence = 0;

        private Incident setType(String type) {
            _type = type;
            return this;
        }

        private String getType() {
            return _type;
        }

        private void incrementOccurrence() {
            _occurrence++;
        }

        private int getOccurrence() {
            return _occurrence;
        }
    }

}
