import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class infoextract {
    private static ArrayList<Template> templates = new ArrayList<>();
    private static ArrayList<String> stories = new ArrayList<>();

    public static void main(String[] args){

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

    /**Parses and creates a template for each story and stores it*/
    private static void generateTemplatesFromStories(){
        Template template;
        for(String story : stories) {
            template = new Template()
                    .setId(findStoryId(story));
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
}
