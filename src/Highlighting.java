import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Highlighting extends SwingWorker<Void,Object> {
    private JTextPane ta;
    private StyleContext style;
    private AttributeSet textStyle;

    public Highlighting(JTextPane text) {
        ta = text;
    }

    private void matching(String str) {
        style = StyleContext.getDefaultStyleContext();
        Color[] colors = {new Color(180, 20, 100), new Color(100,100,100), Color.BLUE, Color.YELLOW,Color.ORANGE,new Color(200,100,100),Color.GREEN};
        textStyle = style.addAttribute(style.getEmptySet(), StyleConstants.Foreground, Color.red);
        String[] regxes = {
                "\\b(new|class|int|void|static|final|public|private|protected|float|if|else|for|while|try|catch|boolean|import|return)\\b",
                "(//.*)",//single line comments
                "([^.\\s]+)\\(",// methods
                "class(.+?)\\{",//class
                "([^\\s(]+) ([^\\s]+) =",//keywords
                "(/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/)", //multiline comments
                "(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")" /*regex from SO*/}; // comment for testings
       //"//.*",
        ta.getStyledDocument().setCharacterAttributes(0,  ta.getDocument().getLength(),style.addAttribute(style.getEmptySet(), StyleConstants.Foreground, Color.WHITE
        ), true );
        String input = str;
        for(int i = 0; i < regxes.length; i++) {
            Pattern p = Pattern.compile(regxes[i], Pattern.MULTILINE);
            Matcher m = p.matcher(input);
            while (m.find())
                ta.getStyledDocument().setCharacterAttributes(m.start(1), (m.end(1) - m.start(1)),style.addAttribute(style.getEmptySet(), StyleConstants.Foreground, colors[i]), true);
        }
    }
    public static void highlightFind(JTextPane textPane, String text, int special) throws BadLocationException {
        Highlighter highlighter = textPane.getHighlighter();
        textPane.getHighlighter().removeAllHighlights();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
        Highlighter.HighlightPainter specialPainter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        int i = textPane.getText(0,  textPane.getDocument().getLength()).indexOf(text);
        while(i >= 0) {
            int p1 = i + text.length();
            highlighter.addHighlight(i, p1, (special == 0)?specialPainter:painter );
            if(special == 0) {
                textPane.setCaretPosition(i);
            }
            i = textPane.getText(0,  textPane.getDocument().getLength()).indexOf(text, i+1);
            special--;
        }
    }
    @Override
    protected Void doInBackground() throws BadLocationException {
        matching(ta.getDocument().getText(0,  ta.getDocument().getLength()));
        return null;
    }

    @Override
    protected void done() {
    }
}