package decompositions;

/**
 * Created by aabhas on 7/22/14.
 */
public class Entity {
  private String word = null;
  private int startOffset = 0;
  private int endOffset = 0;
  private String ner = null;

  public Entity(String text, String ner, int startOffset, int endOffset) {
    this.word = text;
    this.ner = ner;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
  }

  public String getWord() {

    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public void setStartOffset(int startOffset) {
    this.startOffset = startOffset;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  public String getNer() {
    return ner;
  }

  public void setNer(String ner) {
    this.ner = ner;
  }

}
