del gameCanvasUtil\Collision\*.class
del gameCanvasUtil\Resource\*.class
del gameCanvasUtil\*.class
del AppCanvas.class
del GameApp.class
del Game.class

javac gameCanvasUtil\Collision\*.java -encoding SJIS
javac gameCanvasUtil\Resource\*.java -encoding SJIS
javac gameCanvasUtil\*.java -encoding SJIS
javac AppCanvas.java -encoding SJIS
javac GameApp.java -encoding SJIS
javac Game.java -encoding SJIS

if not exist Game.class goto GAME_CLASS_ERROR
jar -cfmv GameApp.jar META-INF\MAINIFEST.MF *.class gameCanvasUtil\*.class res\* gameCanvasUtil\Collision\*.class  gameCanvasUtil\Resource\*.class
java -jar GameApp.jar
:GAME_CLASS_ERROR

pause;
