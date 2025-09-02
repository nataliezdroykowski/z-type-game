import java.util.Random;
import tester.*; 

class Utils {
  
  /* CLASS TEMPLATE
   * 
   * METHODS:
   * this.makeWord(int)                                       -- String
   * this.makeWordForTesting(int, Random)                     -- String
   * this.makeWordHelper(String, int, int)                    -- String
   * this.makeWordForTestingHelper(String, int, int, Random)  -- String
   * this.getLetter(int)                                      -- String
   * this.getRandomX(int)                                     -- int
   * this.getRandomWordLength()                               -- int
   * 
   */
  
  // Makes a random word of the given length
  public String makeWord(int length) {
    return makeWordHelper("", 0, length);
  }
  
  // Uses a seeded random object to be able to test random word generation
  public String makeWordForTesting(int length, Random rand) {
    return makeWordForTestingHelper("", 0, length, rand);
  }
  
  // Adds letters to a string until the string reaches the given length
  public String makeWordHelper(String word, int i, int length) {
    if (i >= length) {
      return word;
    } else {
      return makeWordHelper(word + getLetter(new Random().nextInt(25)), i + 1, length);
    }
  }
  
  // Uses a seeded random object to be able to test making the string
  public String makeWordForTestingHelper(String word, int i, int length, Random rand) {
    if (i >= length) {
      return word;
    } else {
      return makeWordForTestingHelper(word + getLetter(rand.nextInt(25)), i + 1, length, rand);
    }
  }
  
  // Transforms an integer from 0-25 to the corresponding letter
  public String getLetter(int num) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    return "" + alphabet.charAt(num);
  }
  
  // Generates a random X coordinate within the window
  public int getRandomX(int width) {
    return new Random().nextInt(width - 100) + 80;
  }
  
  // Uses a seeded object to test random X coordinate generation
  public int getRandomXTesting(int width, Random rand) {
    return rand.nextInt(width - 100) + 80;
  }
  
  // Generates a random number between 4 and 7 (inclusively)
  public int getRandomWordLength() {
    return new Random().nextInt(4) + 4;
  }
  
  // Uses a seeded object to test random word length generation
  public int getRandomWordLengthTesting(Random rand) {
    return rand.nextInt(4) + 4;
  }
}

//testing the Utils methods
class ExamplesUtil {
  
  Utils u = new Utils();
  
  //testing the random make word method
  boolean testMakeWord(Tester t) {
    return t.checkExpect(u.makeWord(6).length(), 6)
        && t.checkExpect(u.makeWordForTesting(4, new Random(2510)), "nyiu")
        && t.checkExpect(u.makeWordForTesting(6, new Random(2511)), "nmbkyb");
  }
  
  //testing the get letter method
  boolean testGetLetter(Tester t) {
    return t.checkExpect(u.getLetter(22), "w")
        && t.checkExpect(u.getLetter(0), "a")
        && t.checkExpect(u.getLetter(5), "f");
  }
  
  //testing the random x coordinate method
  boolean testRandomX(Tester t) {
    return t.checkExpect(u.getRandomXTesting(500, new Random(2510)), 393)
        && t.checkExpect(u.getRandomXTesting(300, new Random(2510)), 193)
        && t.checkExpect(u.getRandomXTesting(500, new Random(2511)), 418);
  }
  
  //testing the random word length method
  boolean testRandomLength(Tester t) {
    return t.checkExpect(u.getRandomWordLengthTesting(new Random(2510)), 7)
        && t.checkExpect(u.getRandomWordLengthTesting(new Random(2511)), 7);
  }



  
  
}