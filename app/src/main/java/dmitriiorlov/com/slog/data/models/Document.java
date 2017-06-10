package dmitriiorlov.com.slog.data.models;

import java.util.Date;

/**
 * Created by Dmitry on 6/9/2017.
 */

public class Document {

    private String text;
    private long date;

    public Document(){}

    public Document(long date, String text){
        this.date = date;
        this.text = text;
    }

    public String getText(){
        return this.text;
    }
    public String getLowerCaseText(){return this.text.toLowerCase();}
    /**
     * The method gets the epoch time
     * see the answer here: https://stackoverflow.com/questions/37976468/saving-and-retrieving-date-in-firebase
     * @return
     */
    public long getDate(){
        return this.date;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setDate(long date){
        this.date = date;
    }


}
