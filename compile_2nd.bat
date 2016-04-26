del Game.class
javac Game.java -encoding SJIS

if not exist Game.class goto GAME_CLASS_ERROR
jar -cfmv GameApp.jar META-INF\MAINIFEST.MF *.class gameCanvasUtil\*.class res\* gameCanvasUtil\Collision\*.class  gameCanvasUtil\Resource\*.class
java -jar GameApp.jar
:GAME_CLASS_ERROR

pause;
