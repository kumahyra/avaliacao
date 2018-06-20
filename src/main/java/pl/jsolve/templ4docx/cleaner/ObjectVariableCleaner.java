package pl.jsolve.templ4docx.cleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.extractor.KeyExtractor;
import pl.jsolve.templ4docx.extractor.VariablesExtractor;
import pl.jsolve.templ4docx.meta.KeysHolder;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * Field name in object variable may incorrectly starts with upper-case symbol. This class try fix this and change field
 * name with first upper-case (if this variable is not exists) to lower case (if this variable is exists).
 *
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 */
public class ObjectVariableCleaner {

    protected KeyExtractor keyExtractor = new KeyExtractor();
    protected VariablesExtractor extractor = new VariablesExtractor();

    public void clean(Docx docx, Variables variables, VariablePattern variablePattern) {
        List<Key> keys = keyExtractor.extractKeys(variables);
        KeysHolder keysHolder = new KeysHolder(keys);
        XWPFDocument document = docx.getXWPFDocument();
        List<XWPFParagraph> paragraphs = getParagraphs(document);
        for (XWPFParagraph paragraph : paragraphs) {
            cleanParagraph(paragraph, keysHolder, variablePattern);
        }
    }

    /**
     * @param document
     * @return All document paragraphs (including paragraphs in nested tables)
     */
    protected List<XWPFParagraph> getParagraphs(XWPFDocument document) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        paragraphs.addAll(document.getParagraphs());
        for (XWPFTable table : document.getTables()) {
            paragraphs.addAll(getParagraphs(table));
        }
        return paragraphs;
    }

    /**
     * @param table
     * @return All table paragraphs (including paragraphs in nested tables)
     */
    protected List<XWPFParagraph> getParagraphs(XWPFTable table) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                paragraphs.addAll(cell.getParagraphs());
                for (XWPFTable cellTable : cell.getTables()) {
                    paragraphs.addAll(getParagraphs(cellTable));
                }
            }
        }
        return paragraphs;
    }

    /**
     * Fix every invalid not exist variable object to valid existed.
     *
     * @param paragraph
     * @param keys
     * @param variablePattern
     */
    protected void cleanParagraph(XWPFParagraph paragraph, KeysHolder keysHolder, VariablePattern variablePattern) {
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String originalText = run.text();
            String text = originalText;
            if (text == null)
                continue;
            List<String> varNames = extractor.extract(text, variablePattern);

            for (String varName : varNames) {
                if (keysHolder.containsKeyByName(varName)) {
                    continue;
                } else {
                    String fixedVarName = ObjectVariable.fixInvalidFieldName(varName);
                    if (!varName.equals(fixedVarName)) {
                        if (keysHolder.containsKeyByName(fixedVarName)) {
                            // replace only if variable with fixed name exists
                            text = text.replaceAll("\\Q" + varName + "\\E", Matcher.quoteReplacement(fixedVarName));
                        }
                    }
                }
            }
            if (!text.equals(originalText)) {
                replaceRun(paragraph, i, text);
            }
        }
    }

    protected XWPFRun replaceRun(XWPFParagraph paragraph, XWPFRun run, String text) {
        int index = paragraph.getRuns().indexOf(run);
        return replaceRun(paragraph, index, text);
    }

    protected XWPFRun replaceRun(XWPFParagraph paragraph, int index, String text) {
        XWPFRun run = paragraph.getRuns().get(index);
        XWPFRun newRun = paragraph.insertNewRun(index);
        applyStyle(run, newRun);
        newRun.setText(text);
        paragraph.removeRun(index + 1);
        return newRun;
    }

    protected void applyStyle(XWPFRun source, XWPFRun target) {
        applyRPr(target, source.getCTR().getRPr());
    }

    protected void applyRPr(XWPFRun run, CTRPr rPr) {
        CTRPr sourceRPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        sourceRPr.set(rPr);
    }

}
