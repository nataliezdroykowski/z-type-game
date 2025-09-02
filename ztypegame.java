import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

// Represents a the ZType game
class ZTypeWorld extends World {
  
  ILoWord words;
  int timer;
  int score;
  int level;
  
  ZTypeWorld(ILoWord words, int timer, int score, int level) {
    this.words = words;
    this.timer = timer;
    this.score = score;
    this.level = level;
  }
  
  /* CLASS TEMPLATE
   * 
   * FIELDS:
   * this.words                         -- ILoWord
   * this.timer                         -- int
   * this.score                         -- int
   * this.level                         -- int
   * 
   * METHODS:
   * this.makeScene()                   -- WorldScene
   * this.onTick()                      -- World
   * this.onKeyEvent()                  -- World
   * 
   * METHODS (OF FIELDS):
   * this.words.draw(WorldScene)        -- WorldScene
   * this.words.move()                  -- ILoWord
   * this.words.checkAndReduce(String)  -- ILoWord
   * this.words.allInactive()           -- boolean
   * this.words.makeActive(String)      -- ILoWord
   * this.words.atBottom()              -- boolean
   * this.words.filterOutEmpties()      -- ILoWord
   * this.words.addToEnd(IWord)         -- ILoWord
   * this.words.length()                -- int
   */
  
  // Creates the scene
  public WorldScene makeScene() {
    return this.words.draw(new WorldScene(600, 400))
        .placeImageXY(new TextImage("Score: " + this.score, 20, Color.BLACK), 70, 50)
        .placeImageXY(new TextImage("Level: " + this.level, 20, FontStyle.BOLD, Color.BLUE),
            300, 25);
  }
  
  // Updates the game screen each frame where it checks if the player beat the level, if the player
  // lost, if a new word should be added, and if any words are empty
  public World onTick() {
    
    if (this.score >= (500 * this.level + 200 * (this.level - 1))) {
      return new NextLevelWorld(new ZTypeWorld(new MtLoWord(), 0, this.score, this.level + 1));
    }
    
    if (this.words.atBottom()) {
      return new GameOverWorld(this.score);
    }
    
    if (this.timer <= 0) {
      Utils u = new Utils();
      
      if (this.words.filterOutEmpties().length() != this.words.length()) {
        return new ZTypeWorld(this.words.move().filterOutEmpties()
            .addToEnd(new InactiveWord(u.makeWord(u.getRandomWordLength()), 
                u.getRandomX(600), 0)), 26 - 2 * this.level, this.score + 100, this.level);
      } else {
        return new ZTypeWorld(this.words.move().filterOutEmpties()
            .addToEnd(new InactiveWord(u.makeWord(u.getRandomWordLength()), 
                u.getRandomX(600), 0)), 26 - 2 * this.level, this.score, this.level);
      }
      
    }
    
    if (this.words.filterOutEmpties().length() != this.words.length()) {
      return new ZTypeWorld(this.words.move().filterOutEmpties(), 
          this.timer - 1, this.score + 100, this.level);
    } else {
      return new ZTypeWorld(this.words.move().filterOutEmpties(), 
          this.timer - 1, this.score, this.level);
    }
  }
  
  // Handles player trying to type
  public World onKeyEvent(String key) {
    if (words.allInactive()) {
      return new ZTypeWorld(this.words.makeActive(key), this.timer, this.score, this.level);
    }
    else {
      return new ZTypeWorld(this.words.checkAndReduce(key), this.timer, this.score, this.level);
    }
  }
  
}

//Represents the game over state in the ZType game
class GameOverWorld extends World {
  
  int score;
  
  GameOverWorld(int score) {
    this.score = score;
  }
  /* CLASS TEMPLATE
   * FIELDS:
   * this.score                         -- int
   * 
   * METHODS:
   * this.makeScene()                   -- WorldScene
   * this.onKeyEvent()                  -- World
   * 
   */
  
  // Makes the game over screen
  public WorldScene makeScene() {

    return new WorldScene(600, 400)
        .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.RED), 300, 200)
        .placeImageXY(new TextImage("Game over", 50, Color.WHITE), 300, 150)
        .placeImageXY(new TextImage("Final Score: " + this.score, 25, Color.white), 300, 200)
        .placeImageXY(new TextImage("Press space to restart", 15, Color.white), 300, 250);
  }
  
  // Allows the player to restart
  public World onKeyEvent(String key) {
    if (key.equals(" ")) {
      return new ZTypeWorld(new MtLoWord(), 0, 0, 1);
    } else {
      return this;
    }
  }
}

//Represents the waiting for next level state in the ZType game
class NextLevelWorld extends World {
  
  ZTypeWorld nextLevel;
  
  NextLevelWorld(ZTypeWorld nextLevel) {
    this.nextLevel = nextLevel;
  }
  
  /* CLASS TEMPLATE
   * FIELDS:
   * this.nextLevel                     -- ZTypeWorld
   * 
   * METHODS:
   * this.makeScene()                   -- WorldScene
   * this.onKeyEvent()                  -- World
   * 
   */
  
  // Makes the next level screen
  public WorldScene makeScene() {
    return new WorldScene(600, 400)
        .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.BLUE), 300, 200)
        .placeImageXY(new TextImage("Level Completed!", 50, Color.WHITE), 300, 200)
        .placeImageXY(new TextImage("Press space to move on", 15, Color.white), 300, 250);
  }
  
  // Allows the player to continue on to the next level
  public World onKeyEvent(String key) {
    if (key.equals(" ")) {
      return this.nextLevel;
    } else {
      return this;
    }
  }
}

// Represents a list of words
interface ILoWord {
  
  // Draws the first word on the screen
  WorldScene draw(WorldScene ws);
  
  // Moves the words down on the screen
  ILoWord move(); 
  
  // Returns an ILoWord where any active words in this list are reduced by removing the first
  // letter only if the given string matches the first letter
  ILoWord checkAndReduce(String key);
  
  // Checks if all words in list are inactive
  boolean allInactive();
  
  // Activates the first word that's first character matches the given letter
  ILoWord makeActive(String key);
  
  // Checks if any words in the list have reached the bottom of the screen
  boolean atBottom();
  
  // Removes any empty strings from the list
  ILoWord filterOutEmpties();
  
  // Adds a word to the end of the list
  ILoWord addToEnd(IWord word);
  
  // Returns the length of the list
  int length();

}

// Represents an empty list of words
class MtLoWord implements ILoWord {
  
  /* CLASS TEMPLATE
   * 
   * METHODS:
   * this.draw(WorldScene)              -- WorldScene
   * this.move()                        -- ILoWord
   * this.checkAndReduce(String)        -- ILoWord
   * this.allInactive()                 -- boolean
   * this.makeActive(String)            -- ILoWord
   * this.atBottom()                    -- boolean
   * this.filterOutEmpties()            -- ILoWord
   * this.addToEnd(IWord)               -- ILoWord
   * this.length()                      -- int
   * 
   */
  
  MtLoWord() { } 
  
  // Draws the first word on the screen
  public WorldScene draw(WorldScene ws) {
    return ws;
  }
  
  // Moves the words down on the screen
  public ILoWord move() {
    return this;
  }
  
  // Returns an ILoWord where any active words in this list are reduced by removing the first
  // letter only if the given string matches the first letter
  public ILoWord checkAndReduce(String key) {
    return this;
  }
  
  // Checks if all words in list are inactive
  public boolean allInactive() {
    return true;
  }
  
  // Activates the first word that's first character matches the given letter
  public ILoWord makeActive(String key) {
    return this;
  }
  
  // Checks if any words in the list have reached the bottom of the screen
  public boolean atBottom() {
    return false;
  }
  
  // Makes a list that gets rid of the words in this list that are empty strings
  public ILoWord filterOutEmpties() {
    return this;
  }
  
  // Adds a given to the end of the list
  public ILoWord addToEnd(IWord word) {
    /*
     * METHOD TEMPLATE: everything in the class template for ConsLoWord, plus
     *
     * PARAMETERS: 
     * word                                 -- IWord
     *
     * METHODS ON/OF/FOR PARAMETERS: 
     * word.draw(WorldScene)                -- WorldScene
     * word.move()                          -- IWord
     * word.checkAndReduce(String)          -- IWord
     * word.checkActive()                   -- boolean
     * word.startsWith(String)              -- boolean
     * word.makeActive()                    -- ILoWord
     * word.atBottom()                      -- boolean
     * word.isEmpty()                       -- boolean
     * 
     */
    return new ConsLoWord(word, this);
  }
  
  // Returns the length of the list
  public int length() {
    return 0;
  }
}

// Represents a list of words
class ConsLoWord implements ILoWord {
  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* CLASS TEMPLATE
   * 
   * FIELDS:
   * this.first                         -- IWord
   * this.rest                          -- ILoWord
   * 
   * METHODS:
   * this.draw(WorldScene)              -- WorldScene
   * this.move()                        -- ILoWord
   * this.checkAndReduce(String)        -- ILoWord
   * this.allInactive()                 -- boolean
   * this.makeActive(String)            -- ILoWord
   * this.atBottom()                    -- boolean
   * this.filterOutEmpties()            -- ILoWord
   * this.addToEnd(IWord)               -- ILoWord
   * this.length()                      -- int
   * 
   * METHODS (OF FIELDS):
   * this.first.draw(WorldScene)        -- WorldScene
   * this.first.move()                  -- IWord
   * this.first.checkAndReduce(String)  -- IWord
   * this.first.checkActive()           -- boolean
   * this.first.startsWith(String)      -- boolean
   * this.first.makeActive()            -- ILoWord
   * this.first.atBottom()              -- boolean
   * this.first.isEmpty()               -- boolean
   * this.rest.draw(WorldScene)         -- WorldScene
   * this.rest.move()                   -- ILoWord
   * this.rest.checkAndReduce(String)   -- ILoWord
   * this.rest.allInactive()            -- boolean
   * this.rest.makeActive(String)       -- ILoWord
   * this.rest.atBottom()               -- boolean
   * this.rest.filterOutEmpties()       -- ILoWord
   * this.rest.addToEnd(IWord)          -- ILoWord
   * this.rest.length()                 -- int
   * 
   */
  
  // Draws this word on the screen
  public WorldScene draw(WorldScene ws) {
    return this.rest.draw(this.first.draw(ws));
  }
  
  // Moves the words down on the screen
  public ILoWord move() {
    return new ConsLoWord(this.first.move(), this.rest.move());
  }
  
  // Returns an ILoWord where any active words in this list are reduced by removing the first
  // letter only if the given string matches the first letter
  public ILoWord checkAndReduce(String key) {
    return new ConsLoWord(this.first.checkAndReduce(key), this.rest.checkAndReduce(key));
  }
  
  // Checks if all words in list are inactive
  public boolean allInactive() {
    return !this.first.checkActive() && this.rest.allInactive();
  }
  
  // Activates the first word that's first character matches the given letter
  public ILoWord makeActive(String key) {
    if (this.first.startsWith(key)) {
      return new ConsLoWord(this.first.makeActive().checkAndReduce(key), this.rest);
    }
    else {
      return new ConsLoWord(this.first, this.rest.makeActive(key));
    }
  }
  
  // Checks if any words in the list have reached the bottom of the screen
  public boolean atBottom() {
    return this.first.atBottom() || this.rest.atBottom();
  }
  
  // Makes a list that gets rid of the words in this list that are empty strings
  public ILoWord filterOutEmpties() {
    if (this.first.isEmpty()) {
      return this.rest.filterOutEmpties();
    } else {
      return new ConsLoWord(this.first, this.rest.filterOutEmpties());
    }
  }
  
  // Adds a given word to the end of the list
  public ILoWord addToEnd(IWord word) {
    /*
     * METHOD TEMPLATE: everything in the class template for ConsLoWord, plus
     *
     * PARAMETERS: 
     * word                                 -- IWord
     *
     * METHODS ON/OF/FOR PARAMETERS: 
     * word.draw(WorldScene)                -- WorldScene
     * word.move()                          -- IWord
     * word.checkAndReduce(String)          -- IWord
     * word.checkActive()                   -- boolean
     * word.startsWith(String)              -- boolean
     * word.makeActive()                    -- ILoWord
     * word.atBottom()                      -- boolean
     * word.isEmpty()                       -- boolean
     * 
     */
    
    return new ConsLoWord(this.first, this.rest.addToEnd(word));
  }
  
  // Returns the length of the list
  public int length() {
    return 1 + this.rest.length();
  }
}

// Represents a word in the ZType game
interface IWord { 
  
  // Draws the word on the screen
  WorldScene draw(WorldScene ws);
  
  // Moves the word down on the screen
  IWord move();
  
  // Removes the first character of the word if it matches the given character and is active
  IWord checkAndReduce(String key);
  
  // Returns if a word is active or not
  boolean checkActive();
  
  // Checks if the first character of the word matches the given character
  boolean startsWith(String c);
  
  // Turns the word into an active word
  IWord makeActive();
  
  // Checks if a word is at the bottom of the screen
  boolean atBottom();
  
  // Checks if the word is an empty string
  boolean isEmpty();
}

// represents a word in the ZType game
abstract class AWord implements IWord {
  String word;
  int x;
  int y;
  
  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }
  
  // Draws the word on the screen
  public abstract WorldScene draw(WorldScene ws);
  
  // Moves the word down on the screen
  public abstract IWord move();
  
  // Removes the first character of the word if it matches the given character and is active
  public abstract IWord checkAndReduce(String key);
  
  // Returns if a word is active or not
  public abstract boolean checkActive();
  
  // Checks if the first character of the word matches the given character
  public boolean startsWith(String key) {
    if (this.word.length() > 0) {
      return (this.word.substring(0, 1).equals(key));
    } else {
      return false;
    }
  }
  
  // Turns the word into an active word
  public abstract IWord makeActive();
  
  // Checks if a word is at the bottom of the screen
  public boolean atBottom() {
    return this.y > 390;
  }
  
  // Checks to see if this word is an empty string
  public boolean isEmpty() {
    return this.word.equals("");
  }
  
}

// Represents an active word in the ZType game
class ActiveWord extends AWord {
  
  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }
  
  /* CLASS TEMPLATE
   * 
   * FIELDS:
   * this.word                          -- String
   * this.x                             -- int
   * this.y                             -- int
   * 
   * METHODS:
   * this.draw(WorldScene)              -- WorldScene
   * this.move()                        -- IWord
   * this.checkAndReduce(String)        -- IWord
   * this.checkActive()                 -- boolean
   * this.startsWith(String)            -- boolean
   * this.makeActive()                  -- ILoWord
   * this.atBottom()                    -- boolean
   * this.isEmpty()                     -- boolean
   * 
   */
  
  // Draws the word on the screen
  public WorldScene draw(WorldScene ws) {
    return ws.placeImageXY(new TextImage(this.word, 16, FontStyle.BOLD, Color.GREEN), 
        this.x, this.y);
  }
  
  // Moves the word down on the screen
  public IWord move() {
    return new ActiveWord(this.word, this.x, this.y + 5);
  }
  
  // Removes the first character of the word if it matches the given character and is active
  public IWord checkAndReduce(String key) {
    if (this.word.startsWith(key)) {
      return new ActiveWord(this.word.substring(1), this.x, this.y);
    } 
    return this;
  }
  
  // Returns if a word is active or not
  public boolean checkActive() {
    return true;
  }
  
  // Turns the word into an active word
  public IWord makeActive() {
    return this;
  }
}

// Represents an inactive word in the ZType game
class InactiveWord extends AWord {
  
  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }
  
  /* CLASS TEMPLATE
   * 
   * FIELDS:
   * this.word                          -- String
   * this.x                             -- int
   * this.y                             -- int
   * 
   * METHODS:
   * this.draw(WorldScene)              -- WorldScene
   * this.move()                        -- IWord
   * this.checkAndReduce(String)        -- IWord
   * this.checkActive()                 -- boolean
   * this.startsWith(String)            -- boolean
   * this.makeActive()                  -- ILoWord
   * this.atBottom()                    -- boolean
   * this.isEmpty()                     -- boolean
   * 
   */
  
  // Draws the word on the screen
  public WorldScene draw(WorldScene ws) {
    return ws.placeImageXY(new TextImage(this.word, 16, Color.RED), 
        this.x, this.y);
  }
  
  // Moves the word down on the screen
  public IWord move() {
    return new InactiveWord(this.word, this.x, this.y + 5);
  }
  
  // Removes the first character of the word if it matches the given character and is active
  public IWord checkAndReduce(String key) {
    return this;
  }
  
  // Returns if a word is active or not
  public boolean checkActive() {
    return false;
  }
  
  // Turns the word into an active word
  public IWord makeActive() {
    return new ActiveWord(this.word, this.x, this.y);
  }
}

//testing all methods for the game
class Examples {  
  IWord aw1 = new ActiveWord("", 5, 5);
  IWord aw2 = new ActiveWord("hello", 10, 10);
  IWord aw3 = new ActiveWord("world", 15, 15);
  IWord aw4 = new ActiveWord("THERE", 20, 20);
  IWord aw5 = new ActiveWord("ello", 10, 10);
  IWord aw6 = new ActiveWord("orld", 15, 15);
  IWord aw7 = new ActiveWord("HERE", 20, 20);
  IWord aw8 = new ActiveWord("hi", 20, 398);
  
  IWord iaw1 = new InactiveWord("", 5, 10);
  IWord iaw2 = new InactiveWord("hello", 10, 15);
  IWord iaw3 = new InactiveWord("world", 15, 20);
  IWord iaw4 = new InactiveWord("world", 15, 401);
  
  
  IWord test1 = new ActiveWord("hellos", 100, 0);
  IWord test2 = new InactiveWord("worlds", 200, 0);
  IWord test3 = new InactiveWord("today", 15, 400);
  
  MtLoWord mt = new MtLoWord();
  ConsLoWord hellosWorlds = new ConsLoWord(test1, new ConsLoWord(test2, mt));
  ConsLoWord hellosWorlds2 = new ConsLoWord(test1, mt);
  ConsLoWord hellosWorlds3 = new ConsLoWord(iaw1,
      new ConsLoWord(iaw2, new ConsLoWord(iaw3, this.mt)));
  ConsLoWord hellosWorlds4 = new ConsLoWord(test2, mt);
  ConsLoWord hellosWorlds5 = new ConsLoWord(this.test3, 
      new ConsLoWord(new ActiveWord("", 5, 20), this.hellosWorlds2));
  ConsLoWord hellosWorlds6 = new ConsLoWord(this.test1, 
      new ConsLoWord(new ActiveWord("", 5, 20), new ConsLoWord(this.test2,
          new ConsLoWord(new InactiveWord("", 200, 100), this.mt))));
  
  WorldScene ws = new WorldScene(300, 300);
  WorldScene ws2 = new WorldScene(200, 400).
      placeImageXY(new TextImage("testing", 16, FontStyle.BOLD, Color.BLACK), 100, 200);
  WorldScene gameWS = new WorldScene(600,400);
  
  ZTypeWorld start = new ZTypeWorld(this.mt, 0, 0, 1);
  ZTypeWorld level1End = new ZTypeWorld(this.hellosWorlds2, 18, 500, 1);
  ZTypeWorld level2Start = new ZTypeWorld(this.mt, 0, 500, 2);
  ZTypeWorld midEx = new ZTypeWorld(this.hellosWorlds, 14, 2000, 7);
  ZTypeWorld atBottom = new ZTypeWorld(this.hellosWorlds5, 4, 100, 1);
  ZTypeWorld scoreIncrease = new ZTypeWorld(new ConsLoWord(new ActiveWord("", 50, 50), this.mt),
      4, 100, 1);
  ZTypeWorld noneActive = new ZTypeWorld(this.hellosWorlds3, 13, 400, 1);
  
  NextLevelWorld nextLevel2 = new NextLevelWorld(this.level2Start);
  NextLevelWorld nextLevel3 = new NextLevelWorld(new ZTypeWorld(this.mt, 0, 1200, 3));
  NextLevelWorld nextLevel1 = new NextLevelWorld(this.start);
  
  WorldScene nextLevel = new WorldScene(600, 400)
      .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.BLUE), 300, 200)
      .placeImageXY(new TextImage("Level Completed!", 50, Color.WHITE), 300, 200)
      .placeImageXY(new TextImage("Press space to move on", 15, Color.white), 300, 250);
  
  GameOverWorld gameOver1 = new GameOverWorld(200);
  WorldScene gameOver1Screen = new WorldScene(600, 400)
      .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.RED), 300, 200)
      .placeImageXY(new TextImage("Game over", 50, Color.WHITE), 300, 150)
      .placeImageXY(new TextImage("Final Score: " + 200, 25, Color.white), 300, 200)
      .placeImageXY(new TextImage("Press space to restart", 15, Color.white), 300, 250);
  GameOverWorld gameOver2 = new GameOverWorld(500);
  WorldScene gameOver2Screen = new WorldScene(600, 400)
      .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.RED), 300, 200)
      .placeImageXY(new TextImage("Game over", 50, Color.WHITE), 300, 150)
      .placeImageXY(new TextImage("Final Score: " + 500, 25, Color.white), 300, 200)
      .placeImageXY(new TextImage("Press space to restart", 15, Color.white), 300, 250);
  GameOverWorld gameOver3 = new GameOverWorld(0);
  WorldScene gameOver3Screen = new WorldScene(600, 400)
      .placeImageXY(new RectangleImage(600, 400, OutlineMode.SOLID, Color.RED), 300, 200)
      .placeImageXY(new TextImage("Game over", 50, Color.WHITE), 300, 150)
      .placeImageXY(new TextImage("Final Score: " + 0, 25, Color.white), 300, 200)
      .placeImageXY(new TextImage("Press space to restart", 15, Color.white), 300, 250);
  
  //testing the ILoWord draw method
  boolean testDraw(Tester t) {
    return
        t.checkExpect(this.hellosWorlds.draw(this.ws), 
            ws.placeImageXY(new TextImage("hellos", 16, FontStyle.BOLD, Color.GREEN), 100, 0)
            .placeImageXY(new TextImage("worlds", 16, FontStyle.REGULAR, Color.RED), 200, 0))
        && t.checkExpect(this.mt.draw(this.ws), ws);
  }
  
  //testing the ILoWord move method
  boolean testMove(Tester t) {
    return
        t.checkExpect(this.mt.move(), this.mt)
        && t.checkExpect(this.hellosWorlds.move(), 
            new ConsLoWord(new ActiveWord("hellos", 100, 5), 
                new ConsLoWord(new InactiveWord("worlds", 200, 5), this.mt)))
        && t.checkExpect(this.hellosWorlds2.move(), 
            new ConsLoWord(new ActiveWord("hellos", 100, 5), this.mt));
  }
  
  //testing the ILoWord check and reduce method
  boolean testCheckAndReduce(Tester t) {
    return
        t.checkExpect(this.mt.checkAndReduce("h"), this.mt)
        && t.checkExpect(this.hellosWorlds.checkAndReduce("h"), 
            new ConsLoWord(new ActiveWord("ellos", 100, 0), 
                new ConsLoWord(this.test2, this.mt)))
        && t.checkExpect(this.hellosWorlds2.checkAndReduce("w"), this.hellosWorlds2)
        && t.checkExpect(this.hellosWorlds4.checkAndReduce("w"), this.hellosWorlds4);
  }
  
  //testing the ILoWord all inactive method
  boolean testAllInactive(Tester t) {
    return
        t.checkExpect(this.mt.allInactive(), true)
        && t.checkExpect(this.hellosWorlds.allInactive(), false)
        && t.checkExpect(this.hellosWorlds3.allInactive(), true);
    
  }
  
  //testing the ILoWord make active method
  boolean testMakeActive(Tester t) {
    return t.checkExpect(this.mt.makeActive("a"), this.mt)
        && t.checkExpect(this.hellosWorlds.makeActive("h"), 
            new ConsLoWord(new ActiveWord("ellos", 100, 0), 
            new ConsLoWord(this.test2, this.mt)))
        && t.checkExpect(this.hellosWorlds.makeActive("w"),
            new ConsLoWord(this.test1, new ConsLoWord(
                new ActiveWord("orlds", 200, 0), this.mt)))
        && t.checkExpect(this.hellosWorlds.makeActive("r"), this.hellosWorlds);
  }
  
  //testing the ILoWord at bottom method
  boolean testAtBottom(Tester t) {
    return t.checkExpect(this.mt.atBottom(), false)
        && t.checkExpect(this.hellosWorlds3.atBottom(), false)
        && t.checkExpect(this.hellosWorlds.atBottom(), false)
        && t.checkExpect(this.hellosWorlds5.atBottom(), true);
  }
  
  //testing the ILoWord filtering out empties method
  boolean testFilterOutEmpties(Tester t) {
    return t.checkExpect(this.mt.filterOutEmpties(), this.mt)
        && t.checkExpect(this.hellosWorlds.filterOutEmpties(), this.hellosWorlds)
        && t.checkExpect(this.hellosWorlds5.filterOutEmpties(),
            new ConsLoWord(this.test3, this.hellosWorlds2))
        && t.checkExpect(this.hellosWorlds6.filterOutEmpties(), this.hellosWorlds);
  }
  
  //testing the ILoWord add to end method
  boolean testAddToEnd(Tester t) {
    return t.checkExpect(this.mt.addToEnd(this.aw2), new ConsLoWord(this.aw2, this.mt))
        && t.checkExpect(this.hellosWorlds2.addToEnd(this.iaw1), new ConsLoWord(this.test1, 
            new ConsLoWord(this.iaw1, this.mt)))
        && t.checkExpect(this.hellosWorlds.addToEnd(this.test1), new ConsLoWord(this.test1,
            new ConsLoWord(this.test2, new ConsLoWord(this.test1, this.mt))));
  }
  
  //testing the ILoWord length method
  boolean testLength(Tester t) {
    return t.checkExpect(this.mt.length(), 0)
        && t.checkExpect(this.hellosWorlds.length(), 2)
        && t.checkExpect(this.hellosWorlds3.length(), 3);
  }
  
  //testing the IWord draw method
  boolean testIWordDraw(Tester t) {
    return
        t.checkExpect(this.aw1.draw(this.ws), this.ws) 
        && t.checkExpect(this.aw2.draw(this.ws), this.ws.
            placeImageXY(new TextImage("hello", 16, FontStyle.BOLD, Color.GREEN), 10, 10)) 
        && t.checkExpect(this.iaw2.draw(this.ws2), this.ws2.
            placeImageXY(new TextImage("hello", 16, FontStyle.REGULAR, Color.RED), 10, 15)) 
        && t.checkExpect(this.aw3.draw(this.ws), this.ws.
            placeImageXY(new TextImage("world", 16, FontStyle.BOLD, Color.GREEN), 15, 15))
        && t.checkExpect(this.iaw3.draw(this.ws2), this.ws2.
            placeImageXY(new TextImage("world", 16, FontStyle.REGULAR, Color.RED), 15, 20));
  }
  
  //testing the IWord move method
  boolean testIWordMove(Tester t) {
    return
        t.checkExpect(this.aw1.move(), new ActiveWord("", 5, 10)) 
        && t.checkExpect(this.aw2.move(), new ActiveWord("hello", 10, 15)) 
        && t.checkExpect(this.iaw2.move(), new InactiveWord("hello", 10, 20)) 
        && t.checkExpect(this.aw3.move(), new ActiveWord("world", 15, 20))
        && t.checkExpect(this.iaw3.move(), new InactiveWord("world", 15, 25));
  }
  
  //testing the IWord check and reduce method
  boolean testIWordCheckAndReduce(Tester t) {
    return
        t.checkExpect(this.aw1.checkAndReduce("h"), this.aw1) 
        && t.checkExpect(this.aw2.checkAndReduce("h"), this.aw5) 
        && t.checkExpect(this.iaw2.checkAndReduce("h"), this.iaw2) 
        && t.checkExpect(this.aw2.checkAndReduce("w"), this.aw2) 
        && t.checkExpect(this.aw4.checkAndReduce("t"), this.aw4);
  }
  
  //testing the IWord check active method
  boolean testIWordCheckActive(Tester t) {
    return
        t.checkExpect(this.aw1.checkActive(), true) 
        && t.checkExpect(this.aw2.checkActive(), true) 
        && t.checkExpect(this.iaw2.checkActive(), false) 
        && t.checkExpect(this.aw3.checkActive(), true)
        && t.checkExpect(this.iaw3.checkActive(), false);
  }
  
  //testing the IWord starts with method
  boolean testIWordStartsWith(Tester t) {
    return
        t.checkExpect(this.aw1.startsWith("h"), false) 
        && t.checkExpect(this.aw2.startsWith("h"), true) 
        && t.checkExpect(this.iaw2.startsWith("h"), true) 
        && t.checkExpect(this.aw2.startsWith("w"), false) 
        && t.checkExpect(this.aw4.startsWith("t"), false);
  }
  
  //testing the IWord make active method
  boolean testIWordMakeActive(Tester t) {
    return t.checkExpect(this.aw1.makeActive(), new ActiveWord("", 5, 5)) 
        && t.checkExpect(this.aw2.makeActive(), new ActiveWord("hello", 10, 10)) 
        && t.checkExpect(this.iaw2.makeActive(), new ActiveWord("hello", 10, 15)) 
        && t.checkExpect(this.aw3.makeActive(), new ActiveWord("world", 15, 15))
        && t.checkExpect(this.iaw3.makeActive(), new ActiveWord("world", 15, 20));
  }
  
  //testing the IWord at bottom method
  boolean testIWordAtBottom(Tester t) {
    return
        t.checkExpect(this.aw1.atBottom(), false)
        && t.checkExpect(this.iaw2.atBottom(), false)
        && t.checkExpect(this.aw8.atBottom(), true)
        && t.checkExpect(this.iaw4.atBottom(), true);
  }
  
  //testing the IWord is empty method
  boolean testIWordIsEmpty(Tester t) {
    return
        t.checkExpect(this.aw1.isEmpty(), true) 
        && t.checkExpect(this.aw2.isEmpty(), false) 
        && t.checkExpect(this.iaw2.isEmpty(), false) 
        && t.checkExpect(this.aw3.isEmpty(), false)
        && t.checkExpect(this.iaw1.isEmpty(), true);
  }
  
  //testing ZTypeWorld make scene
  boolean testZTMakeScene(Tester t) {
    return t.checkExpect(this.start.makeScene(), this.gameWS
        .placeImageXY(new TextImage("Score: " + 0, 20, Color.BLACK), 70, 50)
          .placeImageXY(new TextImage("Level: " + 1, 20, FontStyle.BOLD, Color.BLUE),
            300, 25))
        && t.checkExpect(this.midEx.makeScene(), this.gameWS.placeImageXY(
            new TextImage("hellos", 16, FontStyle.BOLD, Color.GREEN), 100, 0)
            .placeImageXY(new TextImage("worlds", 16, FontStyle.REGULAR, Color.RED), 200, 0)
            .placeImageXY(new TextImage("Score: " + 2000, 20, Color.BLACK), 70, 50)
            .placeImageXY(new TextImage("Level: " + 7, 20, FontStyle.BOLD, Color.BLUE),
              300, 25))
        && t.checkExpect(this.level1End.makeScene(), this.gameWS.placeImageXY(
            new TextImage("hellos", 16, FontStyle.BOLD, Color.GREEN), 100, 0)
            .placeImageXY(new TextImage("Score: " + 500, 20, Color.BLACK), 70, 50)
            .placeImageXY(new TextImage("Level: " + 1, 20, FontStyle.BOLD, Color.BLUE),
              300, 25));
  }
  
  //testing ZTypeWorld on tick
  
  boolean testZTOnTick(Tester t) {
    return t.checkExpect(this.level1End.onTick(), this.nextLevel2)
        && t.checkExpect(this.midEx.onTick(), new ZTypeWorld(
            new ConsLoWord(new ActiveWord("hellos", 100, 5), 
                new ConsLoWord(new InactiveWord("worlds", 200, 5), mt)), 13, 2000, 7))
        && t.checkExpect(this.atBottom.onTick(),
            new GameOverWorld(100))
        && t.checkExpect(this.scoreIncrease.onTick(), new ZTypeWorld(this.mt, 3, 200, 1));
  }
  
  //testing ZTypeWorld on key event
  boolean testZTOnKey(Tester t) {
    return t.checkExpect(this.midEx.onKeyEvent("k"), this.midEx)
        && t.checkExpect(this.midEx.onKeyEvent("h"), new ZTypeWorld(
            new ConsLoWord(new ActiveWord("ellos", 100, 0), new ConsLoWord(this.test2, this.mt)),
            14, 2000, 7))
        && t.checkExpect(this.midEx.onKeyEvent("w"), this.midEx)
        && t.checkExpect(this.noneActive.onKeyEvent("h"), new ZTypeWorld(
            new ConsLoWord(new InactiveWord("", 5, 10), new ConsLoWord(
                new ActiveWord("ello", 10, 15), new ConsLoWord(new InactiveWord("world", 15, 20),
                    this.mt))), 13, 400, 1));
  }
  
  //testing NextLevelWorld make scene
  boolean testNLWMakeScene(Tester t) {
    return t.checkExpect(this.nextLevel2.makeScene(), this.nextLevel)
        && t.checkExpect(this.nextLevel3.makeScene(), this.nextLevel)
        && t.checkExpect(this.nextLevel1.makeScene(), this.nextLevel);
  }
  
  //testing NextLevelWorld on key event
  boolean testNLWOnKey(Tester t) {
    return t.checkExpect(this.nextLevel2.onKeyEvent("s"), this.nextLevel2)
        && t.checkExpect(this.nextLevel1.onKeyEvent("p"), this.nextLevel1)
        && t.checkExpect(this.nextLevel3.onKeyEvent(" "), new ZTypeWorld(this.mt, 0, 1200, 3));
  }
  
  //testing GameOverWorld make scene
  boolean testGOMakeScene(Tester t) {
    return t.checkExpect(this.gameOver1.makeScene(), this.gameOver1Screen)
        && t.checkExpect(this.gameOver2.makeScene(), this.gameOver2Screen)
        && t.checkExpect(this.gameOver3.makeScene(), this.gameOver3Screen);
  }
  
  //testing GameOverWorld on key event
  boolean testGOKeyEvent(Tester t) {
    return t.checkExpect(this.gameOver1.onKeyEvent("w"), this.gameOver1)
        && t.checkExpect(this.gameOver2.onKeyEvent(" "), new ZTypeWorld(this.mt, 0, 0, 1))
        && t.checkExpect(this.gameOver3.onKeyEvent(" "), new ZTypeWorld(this.mt, 0, 0, 1));
  }
  
  
  boolean testBigBang(Tester t) {
    ZTypeWorld world = new ZTypeWorld(this.mt, 0, 0, 1);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .1;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }
}

