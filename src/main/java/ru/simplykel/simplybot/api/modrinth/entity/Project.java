package ru.simplykel.simplybot.api.modrinth.entity;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;

public class Project {
    // Основная информация
    public String title;
    public String description;
    public String type;
    public String id;
    public String icon;
    // Ссылки
    public String homePage;
    public String discord;
    public String source;
    public String wiki;
    public String issues;
    // Доп. инфа
    public String moderatorMessage;
    public int downloads;
    public int followers;
    // Лицензия
    public String licenceName;
    public String licenceURL;
    public Project(JSONObject json){
        title = json.getString("title");
        description = json.getString("description");
        type = json.getString("project_type");
        if (json.isNull("icon_url")) icon = "https://cdn.discordapp.com/attachments/906948185077973013/1013739797224886344/what.png";
        else icon = json.getString("icon_url");
        id = json.getString("id");
        if(Main.settings.developer) homePage = "https://staging.modrinth.com/"+type+"/"+id;
        else homePage = "https://modrinth.com/"+type+"/"+id;
        if(!json.isNull("discord_url")) discord = json.getString("discord_url");
        if(!json.isNull("wiki_url")) wiki = json.getString("wiki_url");
        if(!json.isNull("source_url")) source = json.getString("source_url");
        if(!json.isNull("issues_url")) issues = json.getString("issues_url");

        downloads = json.getInt("downloads");
        followers = json.getInt("followers");
        JSONObject JSONLicense = json.getJSONObject("license");
        licenceName = JSONLicense.getString("name");
        licenceURL = JSONLicense.getString("url");
    }
}
