package pl.jsolve.templ4docx.strategy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import pl.jsolve.templ4docx.cleaner.TableRowCleaner;
import pl.jsolve.templ4docx.insert.BulletListInsert;
import pl.jsolve.templ4docx.insert.ImageInsert;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.ObjectInsert;
import pl.jsolve.templ4docx.insert.TableCellInsert;
import pl.jsolve.templ4docx.insert.TableRowInsert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.meta.DocumentMetaProcessor;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class TableInsertStrategy implements InsertStrategy {

    private InsertStrategyChooser insertStrategyChooser;
    private TableRowCleaner tableRowCleaner;
    private DocumentMetaProcessor metaProcessor;

    public TableInsertStrategy(InsertStrategyChooser insertStrategyChooser,
            TableRowCleaner tableRowCleaner) {
        this.insertStrategyChooser = insertStrategyChooser;
        this.tableRowCleaner = tableRowCleaner;
        this.metaProcessor = new DocumentMetaProcessor();
    }

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof TableRowInsert)) {
            return;
        }
        if (!(variable instanceof TableVariable)) {
            return;
        }

        TableRowInsert tableRowInsert = (TableRowInsert) insert;
        TableVariable tableVariable = (TableVariable) variable;

        int numberOfRows = tableVariable.getNumberOfRows();

        XWPFTable table = tableRowInsert.getRow().getTable();
        int templateRowPosition = findRowPosition(table.getRows(), tableRowInsert.getRow());
        List<TableCellInsert> templateCellInserts = tableRowInsert.getCellInserts();
        for (int i = numberOfRows-1; i >= 0 ; i--) {
            XWPFTableRow clonedRow = cloneRow(tableRowInsert.getRow());
            for (TableCellInsert cellInsert : templateCellInserts) {

                Insert clonedCellInsert = cloneCellToCopiedRow(tableRowInsert.getRow(), clonedRow, cellInsert);
                Variable subVariable = tableVariable.getVariable(clonedCellInsert.getKey(), i);
                if (subVariable != null) {
                    insertStrategyChooser.replace(clonedCellInsert, subVariable);
                }
            }
            if (i != 0) {
                cleanMetaInformation(clonedRow);
            }
            table.addRow(clonedRow, templateRowPosition + 1);
        }
        tableRowCleaner.add(tableRowInsert.getRow());
    }

    private XWPFTableRow cloneRow(XWPFTableRow templateRow) {
        CTRow ctRow = CTRow.Factory.newInstance();
        ctRow.set(templateRow.getCtRow());
        return new XWPFTableRow(ctRow, templateRow.getTable());
    }

    private List<XWPFParagraph> getParagraphs(XWPFTable table) {
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

    private List<XWPFParagraph> getParagraphs(XWPFTableRow row) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        for (XWPFTableCell cell : row.getTableCells()) {
            paragraphs.addAll(cell.getParagraphs());
            for (XWPFTable cellTable : cell.getTables()) {
                paragraphs.addAll(getParagraphs(cellTable));
            }
        }
        return paragraphs;
    }

    private void cleanMetaInformation(XWPFTableRow row) {
        XWPFDocument document = row.getTable().getBody().getXWPFDocument();
        for (XWPFParagraph paragraph : getParagraphs(row)) {
            for (CTBookmark bookmarkStart : paragraph.getCTP().getBookmarkStartList()) {
                if (metaProcessor.isVarBookmarkName(bookmarkStart)) {
                    BigInteger id = bookmarkStart.getId();
                    CTMarkupRange bookmarkEnd = null;
                    for (CTMarkupRange nextBookmarkEnd : paragraph.getCTP().getBookmarkEndList()) {
                        if (nextBookmarkEnd.getId().equals(id)) {
                            bookmarkEnd = nextBookmarkEnd;
                            break;
                        }
                    }
                    metaProcessor.setNextId(document, bookmarkStart, bookmarkEnd);
                    metaProcessor.setGeneratedByVarBookmarkName(bookmarkStart);
                }
            }
        }
    }

    public void cleanRows() {
        for (XWPFTableRow row : tableRowCleaner.getRows()) {
            try {
                XWPFTable table = row.getTable();
                int rowPosition = findRowPosition(table.getRows(), row);
                table.removeRow(rowPosition);
            } catch (Exception ex) {
                // do nothing, row doesn't exist
            }
        }
    }

    private int findRowPosition(List<XWPFTableRow> rowsOfTable, XWPFTableRow row) {
        for (int i = 0; i < rowsOfTable.size(); i++) {
            if (rowsOfTable.get(i) == row) {
                return i;
            }
        }
        return -1;
    }

    private Insert cloneCellToCopiedRow(XWPFTableRow originalRow, XWPFTableRow copiedRow,
            TableCellInsert tableCellInsert) {
        List<XWPFTableCell> originalCells = originalRow.getTableCells();
        for (int i = 0; i < originalCells.size(); i++) {
            if (originalCells.get(i) == tableCellInsert.getCell()) {
                if (tableCellInsert.getKey().containsSubKey()) {
                    return prepareInsert(copiedRow.getCell(i), tableCellInsert.getKey().getFirstSubKey());
                }
                return prepareInsert(copiedRow.getCell(i), tableCellInsert.getKey());
            }
        }
        return null;
    }

    private Insert prepareInsert(XWPFTableCell cell, Key key) {
        switch (key.getVariableType()) {
        case TEXT:
            return new TextInsert(key, findParagraph(cell, key.getKey()));
        case IMAGE:
            return new ImageInsert(key, findParagraph(cell, key.getKey()));
        case BULLET_LIST:
            return new BulletListInsert(key, findParagraph(cell, key.getKey()), cell, null);
        case OBJECT:
            return new ObjectInsert(key, findParagraph(cell, key.getKey()));
		default:
			break;
        }
        return null;
    }

    private XWPFParagraph findParagraph(XWPFTableCell cell, String key) {
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            if (StringUtils.contains(paragraph.getText(), key)) {
                return paragraph;
            }
        }
        return null;
    }

}
