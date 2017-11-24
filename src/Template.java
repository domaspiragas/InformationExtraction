import java.util.ArrayList;

public class Template {
    public Template(){}

    private String _id, _incident;
    private ArrayList<String>
            _weapons = new ArrayList<>(),
            _perpIndivs = new ArrayList<>(),
            _perpOrgs = new ArrayList<>(),
            _targets = new ArrayList<>(),
            _victims = new ArrayList<>();

    public Template setId(String id){
        _id = id;
        return this;
    }
    public String getId(){
        return _id;
    }

    public Template setIncident(String incident){
        _incident = incident;
        return this;
    }
    public String getIncident(){
        return _incident;
    }

    public Template addWeapon(String weapon){
        _weapons.add(weapon);
        return this;
    }
    public void removeWeapon(String weapon){
        _weapons.remove(weapon);
    }
    public ArrayList<String> getWeapons(){
        return _weapons;
    }

    public Template addPerpIndiv(String perpIndiv){
        _perpIndivs.add(perpIndiv);
        return this;
    }
    public void removePerpIndiv(String perpIndiv){
        _perpIndivs.remove(perpIndiv);
    }
    public ArrayList<String> getPerpIndivs() {
        return _perpIndivs;
    }

    public Template addPerpOrg(String perpOrg){
        _perpOrgs.add(perpOrg);
        return this;
    }
    public void removePerpOrg(String perpOrg){
        _perpOrgs.remove(perpOrg);
    }
    public ArrayList<String> getPerpOrgs() {
        return _perpOrgs;
    }

    public Template addTarget(String target){
        _targets.add(target);
        return this;
    }
    public void removeTarget(String target){
        _targets.remove(target);
    }
    public ArrayList<String> getTargets() {
        return _targets;
    }

    public Template addVictim(String victim){
        _victims.add(victim);
        return this;
    }
    public void removeVictim(String victim){
        _victims.remove(victim);
    }
    public ArrayList<String> getVictims() {
        return _victims;
    }

    @Override
    public String toString(){
        String STRING_FORMAT = "%-15s%-15s\n";
        boolean first = true;
        StringBuilder returnString = new StringBuilder();

        returnString.append(String.format(STRING_FORMAT, "ID:", _id));
        returnString.append(String.format(STRING_FORMAT, "INCIDENT:", _incident));

        if(_weapons.isEmpty()){
            returnString.append(String.format(STRING_FORMAT, "WEAPON:", "-"));
        } else {
            for (String weapon : _weapons) {
                if (first) {
                    returnString.append(String.format(STRING_FORMAT, "WEAPON:", weapon));
                    first = false;
                }else {
                    returnString.append(String.format(STRING_FORMAT, "", weapon));
                }
            }
            first = true;
        }

        if(_perpIndivs.isEmpty()){
            returnString.append(String.format(STRING_FORMAT, "PERP INDIV:", "-"));
        }else {
            for (String perpIndiv : _perpIndivs) {
                if (first) {
                    returnString.append(String.format(STRING_FORMAT, "PERP INDIV:", perpIndiv));
                    first = false;
                } else {
                    returnString.append(String.format(STRING_FORMAT, "", perpIndiv));
                }
            }
            first = true;
        }

        if(_perpOrgs.isEmpty()){
            returnString.append(String.format(STRING_FORMAT, "PERP ORG:", "-"));
        }else {
            for (String perpOrg : _perpOrgs) {
                if (first) {
                    returnString.append(String.format(STRING_FORMAT, "PERP ORG:", perpOrg));
                    first = false;
                } else {
                    returnString.append(String.format(STRING_FORMAT, "", perpOrg));
                }
            }
            first = true;
        }

        if(_targets.isEmpty()){
            returnString.append(String.format(STRING_FORMAT, "TARGET:", "-"));
        }else {
            for (String target : _targets) {
                if (first) {
                    returnString.append(String.format(STRING_FORMAT, "TARGET:", target));
                    first = false;
                } else {
                    returnString.append(String.format(STRING_FORMAT, "", target));
                }
            }
            first = true;
        }

        if(_victims.isEmpty()){
            returnString.append(String.format(STRING_FORMAT, "VICTIM:", "-"));
        }else {
            for (String victim : _victims) {
                if (first) {
                    returnString.append(String.format(STRING_FORMAT, "VICTIM:", victim));
                    first = false;
                } else {
                    returnString.append(String.format(STRING_FORMAT, "", victim));
                }
            }
        }
        return returnString.toString();
    }
}
