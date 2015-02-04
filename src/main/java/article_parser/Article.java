
package article_parser;

public class Article {

  private String text;

  public Article() {

  }


  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "<article>" + this.text + "</article>";
  }
}