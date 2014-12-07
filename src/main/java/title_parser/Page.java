
package title_parser;

public class Page {

    private int id;
    private String title;
    private String text;

    public Page() {

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "<title>" + this.title + "</title> \n <article>" + this.text + "</article>";
    }
}