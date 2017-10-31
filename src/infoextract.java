import java.io.PrintWriter;
import java.util.ArrayList;

public class infoextract {
    private static ArrayList<Template> templates = new ArrayList<>();

    public static void main(String[] args){

        //Testing output format
        Template temp = new Template();

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

        createTemplatesFile("news.txt");
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
}
