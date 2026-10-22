package smartcityx.service;

import smartcityx.algorithms.suffix.*;
import smartcityx.core.MyArrayList;
import smartcityx.model.CityDocument;

/**
 * Service orchestrating Suffix Structures (M2 - CO2) for municipal document indexing & similarity.
 */
public class DocumentIntelligenceService {

    public static class IndexResult {
        public final int[] suffixArray;
        public final int[] lcpArray;
        public final String longestRepeatedClause;
        public final int distinctSubstrings;

        public IndexResult(int[] suffixArray, int[] lcpArray, String longestRepeatedClause, int distinctSubstrings) {
            this.suffixArray = suffixArray;
            this.lcpArray = lcpArray;
            this.longestRepeatedClause = longestRepeatedClause;
            this.distinctSubstrings = distinctSubstrings;
        }
    }

    public IndexResult indexDocumentSAIS(CityDocument doc) {
        String text = doc.getFullText();
        int[] sa = SAIS.buildSA(text);
        int[] lcp = KasaiLCP.buildLCP(text, sa);
        String longestRep = KasaiLCP.findLongestRepeatedSubstring(text, sa, lcp);

        SuffixAutomaton sam = new SuffixAutomaton(text);
        int distinctCount = sam.getDistinctSubstringsCount();

        return new IndexResult(sa, lcp, longestRep, distinctCount);
    }

    public MyArrayList<Integer> searchDocSubstrings(CityDocument doc, String pattern) {
        SuffixArray sa = new SuffixArray(doc.getFullText());
        return sa.findAllOccurrences(pattern);
    }

    public boolean verifySubstringWithSAM(CityDocument doc, String pattern) {
        SuffixAutomaton sam = new SuffixAutomaton(doc.getFullText());
        return sam.containsSubstring(pattern);
    }
}
