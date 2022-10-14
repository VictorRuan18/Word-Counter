import components.map.Map;
import components.map.Map1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Counts word occurrences in a given input file and outputs an HTML document
 * with a table of the words and counts listed in alphabetical order.
 *
 * @author Victor Ruan
 */
public final class WordCounter {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private WordCounter() {

    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */

    public static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        strSet.clear();
        for (int i = 0; i < str.length(); i++) {
            if (!strSet.contains(str.charAt(i))) {
                strSet.add(str.charAt(i));
            }
        }

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int count = 0;

        String result = "";

        if (separators.contains(text.charAt(position))) {
            while (count < text.substring(position, text.length()).length()) {
                if (separators.contains(text.charAt(position + count))) {
                    result = result + text.charAt(position + count);
                    count++;
                } else {
                    count = text.substring(position, text.length()).length();
                }
            }

        } else {
            while (count < text.substring(position, text.length()).length()) {
                if (!separators.contains(text.charAt(position + count))) {
                    result = result + text.charAt(position + count);
                    count++;
                } else {
                    count = text.substring(position, text.length()).length();
                }
            }

        }
        return result;
    }

    /**
     * Adds words and counts into a map
     *
     * @param mapWC
     *            the map with their words and counts.
     * @param in
     *            the input stream
     * @ensures mapWithWC = words and counts given in the input file.
     */
    public static Map<String, Integer> mapWithWC(Set<String> words,
            SimpleReader in) {
        Map<String, Integer> mapWC = new Map1L<String, Integer>();

        while (!in.atEOS()) {

            String separator = " ,";
            Set<Character> separatorSet = new Set1L<>();

            //creating a separator set
            generateElements(separator, separatorSet);

            String nextLine = in.nextLine();
            nextLine = nextLine.replaceAll("[^a-zA-Z]", " ");
            int i = 0;
            //checking the line of text
            while (i < nextLine.length()) {
                String wordOrSep = nextWordOrSeparator(nextLine, i,
                        separatorSet);
                //skip to next character if its not a letter
                if (!Character.isLetter(wordOrSep.charAt(0))) {
                    i = i + wordOrSep.length();

                } else {
                    if (mapWC.hasKey(wordOrSep)) {
                        mapWC.replaceValue(wordOrSep,
                                mapWC.value(wordOrSep) + 1);

                    } else {
                        //adds word into a map with count of 1
                        mapWC.add(wordOrSep, 1);
                        //adds word into a set
                        words.add(wordOrSep);
                    }
                    i = i + wordOrSep.length();
                }

            }

        }

        return mapWC;
    }

    /**
     * Takes the set of terms and get the first lexicographic string in the set
     *
     * @param terms
     *            the given set of terms
     * @replaces terms
     * @ensures terms = original terms set except the first word alphabetically.
     * @return the smallest term in the set
     */
    public static String getFirstLexiStr(Set<String> terms) {

        Set<String> termsTemp = new Set1L<>();
        String smallest = "";

        //while there are still terms to loop through
        while (terms.size() != 0 && smallest.equals("")) {
            int check = 0;
            //take out a word from terms to compare.
            String temp = terms.removeAny();

            //compare a term to each word in terms
            for (String word : terms) {
                if (temp.compareToIgnoreCase(word) > 0) {
                    check = 1;
                }
            }

            /*
             * if temp is smallest word in current terms set, get smallest term,
             * end this inner loop, and add back term/terms that were taken out
             */
            if (check == 0) {
                smallest = temp;
                terms.add(termsTemp);

            } else if (check == 1) {
                //add the word that were removed from terms into the termsTemp set (if smallest word isn't found)
                termsTemp.add(temp);
            }
        }
        //returns smallest term in the terms set
        return smallest;
    }

    /**
     * Generates the HTML output file with the name of the input file at the top
     *
     * @param userInput
     *            the name of the file the user enters
     * @param alphaSeq
     *            sequence of terms in alphabetical order
     * @param termsCount
     *            the array of the word counts
     * @param folderName
     *            the output file location
     * @param inFile
     *            reads the input file
     * @ensures generatePage includes title and table of words with their own
     *          counts.
     */
    public static void generatePage(String userInput, Sequence<String> alphaSeq,
            int[] termsCount, String folderName, SimpleReader inFile) {
        //creating index.html file and saving it to the folder the user provides
        SimpleWriter mainPage = new SimpleWriter1L(folderName + "/index.html");

        //creating header
        mainPage.println("<html>");
        mainPage.println("<head>");
        mainPage.println(
                "<title>" + "Words Counted in" + userInput + "</title>");
        mainPage.println("</head>");

        mainPage.println("<body>");
        mainPage.println("<h2>" + "Words Counted in " + userInput + "</h2>");
        mainPage.println("<hr>");
        mainPage.println("<table border='1'>");

        mainPage.println("<tr>");
        mainPage.println("<th>Words</th>");
        mainPage.println("<th>Counts</th>");
        mainPage.println("</tr>");

        //creates rows for each word and it's word occurrences.
        for (int i = 0; i < alphaSeq.length(); i++) {
            mainPage.println("<tr>");
            mainPage.println("<td>" + alphaSeq.entry(i));
            mainPage.println("<td>" + termsCount[i]);
            mainPage.println("</tr>");
        }

        mainPage.println("</tr>");
        mainPage.println("</table>");
        mainPage.println("</body>");
        mainPage.println("</html>");
        mainPage.close();

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();
        out.print("Enter name of an input file: ");
        String userInput = in.nextLine();
        SimpleReader inFile = new SimpleReader1L(userInput);

        out.print("Enter name of a folder: ");
        String outputFile = in.nextLine();

        Set<String> terms = new Set1L<String>();
        //get map with words and its counts AND get all of its terms in a set
        Map<String, Integer> mapWC = mapWithWC(terms, inFile);
        int[] termCount = new int[terms.size()];
        Sequence<String> seqOfTerms = new Sequence1L<>();

        /*
         * Generates a sequence of the words in alphabetical order and generates
         * an array of each word's count that correspond to each element in the
         * words sequence.
         */
        int i = 0;
        while (terms.size() > 0) {
            String nextTerm = getFirstLexiStr(terms);
            seqOfTerms.add(seqOfTerms.length(), nextTerm);
            termCount[i] = mapWC.value(nextTerm);
            i++;
        }
        generatePage(userInput, seqOfTerms, termCount, outputFile, inFile);

        in.close();
        out.close();
    }

}
