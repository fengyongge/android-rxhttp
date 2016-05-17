
package app.netframe.bean;
public class UrlBean {

    private String apiUri;
    private String noncestr;
    private String time;
    private String version;


    public UrlBean() {
        super();
    }



    public UrlBean(String apiUri, String noncestr, String time,
                   String version) {
        super();
        this.apiUri = apiUri;
        this.noncestr = noncestr;
        this.time = time;
        this.version = version;
    }



    public String getApiUri() {
        return apiUri;
    }
    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }
    public String getNoncestr() {
        return noncestr;
    }
    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }



    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }



}
