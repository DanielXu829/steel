//
// Source code recreated from quartz .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula.Factory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 重写
 * 移除cell检测 ：checkFormulaCachedValueType方法 的执行
 */
@SuppressWarnings("All")
public final class XSSFCell implements Cell {
    private static final String FALSE_AS_STRING = "0";
    private static final String TRUE_AS_STRING = "1";
    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";
    private CTCell _cell;
    private final XSSFRow _row;
    private int _cellNum;
    private SharedStringsTable _sharedStringSource;
    private StylesTable _stylesSource;

    protected XSSFCell(XSSFRow row, CTCell cell) {
        this._cell = cell;
        this._row = row;
        if (cell.getR() != null) {
            this._cellNum = (new CellReference(cell.getR())).getCol();
        } else {
            int prevNum = row.getLastCellNum();
            if (prevNum != -1) {
                this._cellNum = row.getCell(prevNum - 1, MissingCellPolicy.RETURN_NULL_AND_BLANK).getColumnIndex() + 1;
            }
        }

        this._sharedStringSource = row.getSheet().getWorkbook().getSharedStringSource();
        this._stylesSource = row.getSheet().getWorkbook().getStylesSource();
    }

    @Internal
    public void copyCellFrom(Cell srcCell, CellCopyPolicy policy) {
        if (policy.isCopyCellValue()) {
            if (srcCell != null) {
                CellType copyCellType = srcCell.getCellTypeEnum();
                if (copyCellType == CellType.FORMULA && !policy.isCopyCellFormula()) {
                    copyCellType = srcCell.getCachedFormulaResultTypeEnum();
                }

                switch (copyCellType) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(srcCell)) {
                            this.setCellValue(srcCell.getDateCellValue());
                        } else {
                            this.setCellValue(srcCell.getNumericCellValue());
                        }
                        break;
                    case STRING:
                        this.setCellValue(srcCell.getStringCellValue());
                        break;
                    case FORMULA:
                        this.setCellFormula(srcCell.getCellFormula());
                        break;
                    case BLANK:
                        this.setBlank();
                        break;
                    case BOOLEAN:
                        this.setCellValue(srcCell.getBooleanCellValue());
                        break;
                    case ERROR:
                        this.setCellErrorValue(srcCell.getErrorCellValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid cell type " + srcCell.getCellTypeEnum());
                }
            } else {
                this.setBlank();
            }
        }

        if (policy.isCopyCellStyle()) {
            if (srcCell != null) {
                this.setCellStyle(srcCell.getCellStyle());
            } else {
                this.setCellStyle((CellStyle) null);
            }
        }

        Hyperlink srcHyperlink;
        if (policy.isMergeHyperlink()) {
            srcHyperlink = srcCell.getHyperlink();
            if (srcHyperlink != null) {
                this.setHyperlink(new XSSFHyperlink(srcHyperlink));
            }
        } else if (policy.isCopyHyperlink()) {
            srcHyperlink = srcCell.getHyperlink();
            if (srcHyperlink == null) {
                this.setHyperlink((Hyperlink) null);
            } else {
                this.setHyperlink(new XSSFHyperlink(srcHyperlink));
            }
        }

    }

    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }

    protected StylesTable getStylesSource() {
        return this._stylesSource;
    }

    public XSSFSheet getSheet() {
        return this.getRow().getSheet();
    }

    public XSSFRow getRow() {
        return this._row;
    }

    public boolean getBooleanCellValue() {
        CellType cellType = this.getCellTypeEnum();
        switch (cellType) {
            case FORMULA:
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            case BLANK:
                return false;
            case BOOLEAN:
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            default:
                throw typeMismatch(CellType.BOOLEAN, cellType, false);
        }
    }

    public void setCellValue(boolean value) {
        this._cell.setT(STCellType.B);
        this._cell.setV(value ? "1" : "0");
    }

    public double getNumericCellValue() {
        CellType cellType = this.getCellTypeEnum();
        switch (cellType) {
            case NUMERIC:
            case FORMULA:
                if (this._cell.isSetV()) {
                    String v = this._cell.getV();
                    if (v.isEmpty()) {
                        return 0.0D;
                    }

                    try {
                        return Double.parseDouble(v);
                    } catch (NumberFormatException var4) {
                        throw typeMismatch(CellType.NUMERIC, CellType.STRING, false);
                    }
                }

                return 0.0D;
            case STRING:
            default:
                throw typeMismatch(CellType.NUMERIC, cellType, false);
            case BLANK:
                return 0.0D;
        }
    }

    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.DIV0.getString());
        } else if (Double.isNaN(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.NUM.getString());
        } else {
            this._cell.setT(STCellType.N);
            this._cell.setV(String.valueOf(value));
        }

    }

    public String getStringCellValue() {
        return this.getRichStringCellValue().getString();
    }

    public XSSFRichTextString getRichStringCellValue() {
        CellType cellType = this.getCellTypeEnum();
        XSSFRichTextString rt;
        switch (cellType) {
            case STRING:
                if (this._cell.getT() == STCellType.INLINE_STR) {
                    if (this._cell.isSetIs()) {
                        rt = new XSSFRichTextString(this._cell.getIs());
                    } else if (this._cell.isSetV()) {
                        rt = new XSSFRichTextString(this._cell.getV());
                    } else {
                        rt = new XSSFRichTextString("");
                    }
                } else if (this._cell.getT() == STCellType.STR) {
                    rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                } else if (this._cell.isSetV()) {
                    int idx = Integer.parseInt(this._cell.getV());
                    rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(idx));
                } else {
                    rt = new XSSFRichTextString("");
                }
                break;
            case FORMULA:
//                checkFormulaCachedValueType(CellType.STRING, this.getBaseCellType(false));
                rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                break;
            case BLANK:
                rt = new XSSFRichTextString("");
                break;
            default:
                throw typeMismatch(CellType.STRING, cellType, false);
        }

        rt.setStylesTableReference(this._stylesSource);
        return rt;
    }

    private static void checkFormulaCachedValueType(CellType expectedTypeCode, CellType cachedValueType) {
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }

    public void setCellValue(String str) {
        this.setCellValue((RichTextString) (str == null ? null : new XSSFRichTextString(str)));
    }

    public void setCellValue(RichTextString str) {
        if (str != null && str.getString() != null) {
            if (str.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
                throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
            } else {
                CellType cellType = this.getCellTypeEnum();
                switch (cellType) {
                    case FORMULA:
                        this._cell.setV(str.getString());
                        this._cell.setT(STCellType.STR);
                        break;
                    default:
                        if (this._cell.getT() == STCellType.INLINE_STR) {
                            this._cell.setV(str.getString());
                        } else {
                            this._cell.setT(STCellType.S);
                            XSSFRichTextString rt = (XSSFRichTextString) str;
                            rt.setStylesTableReference(this._stylesSource);
                            int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                            this._cell.setV(Integer.toString(sRef));
                        }
                }

            }
        } else {
            this.setCellType(CellType.BLANK);
        }
    }

    public String getCellFormula() {
        return this.getCellFormula((XSSFEvaluationWorkbook) null);
    }

    protected String getCellFormula(XSSFEvaluationWorkbook fpb) {
        CellType cellType = this.getCellTypeEnum();
        if (cellType != CellType.FORMULA) {
            throw typeMismatch(CellType.FORMULA, cellType, false);
        } else {
            CTCellFormula f = this._cell.getF();
            if (this.isPartOfArrayFormulaGroup() && f == null) {
                XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
                return cell.getCellFormula(fpb);
            } else {
                return f.getT() == STCellFormulaType.SHARED ? this.convertSharedFormula((int) f.getSi(), fpb == null ? XSSFEvaluationWorkbook.create(this.getSheet().getWorkbook()) : fpb) : f.getStringValue();
            }
        }
    }

    private String convertSharedFormula(int si, XSSFEvaluationWorkbook fpb) {
        XSSFSheet sheet = this.getSheet();
        CTCellFormula f = sheet.getSharedFormula(si);
        if (f == null) {
            throw new IllegalStateException("Master cell of quartz shared formula with sid=" + si + " was not found");
        } else {
            String sharedFormula = f.getStringValue();
            String sharedFormulaRange = f.getRef();
            CellRangeAddress ref = CellRangeAddress.valueOf(sharedFormulaRange);
            int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
            SharedFormula sf = new SharedFormula(SpreadsheetVersion.EXCEL2007);
            Ptg[] ptgs = FormulaParser.parse(sharedFormula, fpb, FormulaType.CELL, sheetIndex, this.getRowIndex());
            Ptg[] fmla = sf.convertSharedFormulas(ptgs, this.getRowIndex() - ref.getFirstRow(), this.getColumnIndex() - ref.getFirstColumn());
            return FormulaRenderer.toFormulaString(fpb, fmla);
        }
    }

    public void setCellFormula(String formula) {
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }

        this.setFormula(formula, FormulaType.CELL);
    }

    void setCellArrayFormula(String formula, CellRangeAddress range) {
        this.setFormula(formula, FormulaType.ARRAY);
        CTCellFormula cellFormula = this._cell.getF();
        cellFormula.setT(STCellFormulaType.ARRAY);
        cellFormula.setRef(range.formatAsString());
    }

    private void setFormula(String formula, FormulaType formulaType) {
        XSSFWorkbook wb = this._row.getSheet().getWorkbook();
        if (formula == null) {
            wb.onDeleteFormula(this);
            if (this._cell.isSetF()) {
                this._cell.unsetF();
            }

        } else {
            XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
            FormulaParser.parse(formula, fpb, formulaType, wb.getSheetIndex(this.getSheet()), this.getRowIndex());
            CTCellFormula f = Factory.newInstance();
            f.setStringValue(formula);
            this._cell.setF(f);
            if (this._cell.isSetV()) {
                this._cell.unsetV();
            }

        }
    }

    public int getColumnIndex() {
        return this._cellNum;
    }

    public int getRowIndex() {
        return this._row.getRowNum();
    }

    public String getReference() {
        String ref = this._cell.getR();
        return ref == null ? this.getAddress().formatAsString() : ref;
    }

    public CellAddress getAddress() {
        return new CellAddress(this);
    }

    public XSSFCellStyle getCellStyle() {
        XSSFCellStyle style = null;
        if (this._stylesSource.getNumCellStyles() > 0) {
            long idx = this._cell.isSetS() ? this._cell.getS() : 0L;
            style = this._stylesSource.getStyleAt((int) idx);
        }

        return style;
    }

    public void setCellStyle(CellStyle style) {
        if (style == null) {
            if (this._cell.isSetS()) {
                this._cell.unsetS();
            }
        } else {
            XSSFCellStyle xStyle = (XSSFCellStyle) style;
            xStyle.verifyBelongsToStylesSource(this._stylesSource);
            long idx = (long) this._stylesSource.putStyle(xStyle);
            this._cell.setS(idx);
        }

    }

    private boolean isFormulaCell() {
        return this._cell.getF() != null || this.getSheet().isCellInArrayFormulaContext(this);
    }

    /**
     * @deprecated
     */
    public int getCellType() {
        return this.getCellTypeEnum().getCode();
    }

    /**
     * @deprecated
     */
    @Internal(
            since = "POI 3.15 beta 3"
    )
    public CellType getCellTypeEnum() {
        return this.isFormulaCell() ? CellType.FORMULA : this.getBaseCellType(true);
    }

    /**
     * @deprecated
     */
    public int getCachedFormulaResultType() {
        return this.getCachedFormulaResultTypeEnum().getCode();
    }

    /**
     * @deprecated
     */
    public CellType getCachedFormulaResultTypeEnum() {
        if (!this.isFormulaCell()) {
            throw new IllegalStateException("Only formula cells have cached results");
        } else {
            return this.getBaseCellType(false);
        }
    }

    private CellType getBaseCellType(boolean blankCells) {
        switch (this._cell.getT().intValue()) {
            case 1:
                return CellType.BOOLEAN;
            case 2:
                if (!this._cell.isSetV() && blankCells) {
                    return CellType.BLANK;
                }

                return CellType.NUMERIC;
            case 3:
                return CellType.ERROR;
            case 4:
            case 5:
            case 6:
                return CellType.STRING;
            default:
                throw new IllegalStateException("Illegal cell type: " + this._cell.getT());
        }
    }

    public Date getDateCellValue() {
        if (this.getCellTypeEnum() == CellType.BLANK) {
            return null;
        } else {
            double value = this.getNumericCellValue();
            boolean date1904 = this.getSheet().getWorkbook().isDate1904();
            return DateUtil.getJavaDate(value, date1904);
        }
    }

    public void setCellValue(Date value) {
        if (value == null) {
            this.setCellType(CellType.BLANK);
        } else {
            boolean date1904 = this.getSheet().getWorkbook().isDate1904();
            this.setCellValue(DateUtil.getExcelDate(value, date1904));
        }
    }

    public void setCellValue(Calendar value) {
        if (value == null) {
            this.setCellType(CellType.BLANK);
        } else {
            boolean date1904 = this.getSheet().getWorkbook().isDate1904();
            this.setCellValue(DateUtil.getExcelDate(value, date1904));
        }
    }

    public String getErrorCellString() throws IllegalStateException {
        CellType cellType = this.getBaseCellType(true);
        if (cellType != CellType.ERROR) {
            throw typeMismatch(CellType.ERROR, cellType, false);
        } else {
            return this._cell.getV();
        }
    }

    public byte getErrorCellValue() throws IllegalStateException {
        String code = this.getErrorCellString();
        if (code == null) {
            return 0;
        } else {
            try {
                return FormulaError.forString(code).getCode();
            } catch (IllegalArgumentException var3) {
                throw new IllegalStateException("Unexpected error code", var3);
            }
        }
    }

    public void setCellErrorValue(byte errorCode) {
        FormulaError error = FormulaError.forInt(errorCode);
        this.setCellErrorValue(error);
    }

    public void setCellErrorValue(FormulaError error) {
        this._cell.setT(STCellType.E);
        this._cell.setV(error.getString());
    }

    public void setAsActiveCell() {
        this.getSheet().setActiveCell(this.getAddress());
    }

    private void setBlank() {
        CTCell blank = org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell.Factory.newInstance();
        blank.setR(this._cell.getR());
        if (this._cell.isSetS()) {
            blank.setS(this._cell.getS());
        }

        this._cell.set(blank);
    }

    protected void setCellNum(int num) {
        checkBounds(num);
        this._cellNum = num;
        String ref = (new CellReference(this.getRowIndex(), this.getColumnIndex())).formatAsString();
        this._cell.setR(ref);
    }

    /**
     * @deprecated
     */
    public void setCellType(int cellType) {
        this.setCellType(CellType.forInt(cellType));
    }

    public void setCellType(CellType cellType) {
        CellType prevType = this.getCellTypeEnum();
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }

        if (prevType == CellType.FORMULA && cellType != CellType.FORMULA) {
            this.getSheet().getWorkbook().onDeleteFormula(this);
        }

        String str;
        switch (cellType) {
            case NUMERIC:
                this._cell.setT(STCellType.N);
                break;
            case STRING:
                if (prevType != CellType.STRING) {
                    str = this.convertCellValueToString();
                    XSSFRichTextString rt = new XSSFRichTextString(str);
                    rt.setStylesTableReference(this._stylesSource);
                    int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                    this._cell.setV(Integer.toString(sRef));
                }

                this._cell.setT(STCellType.S);
                break;
            case FORMULA:
                if (!this._cell.isSetF()) {
                    CTCellFormula f = Factory.newInstance();
                    f.setStringValue("0");
                    this._cell.setF(f);
                    if (this._cell.isSetT()) {
                        this._cell.unsetT();
                    }
                }
                break;
            case BLANK:
                this.setBlank();
                break;
            case BOOLEAN:
                str = this.convertCellValueToBoolean() ? "1" : "0";
                this._cell.setT(STCellType.B);
                this._cell.setV(str);
                break;
            case ERROR:
                this._cell.setT(STCellType.E);
                break;
            default:
                throw new IllegalArgumentException("Illegal cell type: " + cellType);
        }

        if (cellType != CellType.FORMULA && this._cell.isSetF()) {
            this._cell.unsetF();
        }

    }

    public String toString() {
        switch (this.getCellTypeEnum()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(this)) {
                    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(this.getDateCellValue());
                }

                return Double.toString(this.getNumericCellValue());
            case STRING:
                return this.getRichStringCellValue().toString();
            case FORMULA:
                return this.getCellFormula();
            case BLANK:
                return "";
            case BOOLEAN:
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            case ERROR:
                return ErrorEval.getText(this.getErrorCellValue());
            default:
                return "Unknown Cell Type: " + this.getCellTypeEnum();
        }
    }

    public String getRawValue() {
        return this._cell.getV();
    }

    private static RuntimeException typeMismatch(CellType expectedType, CellType actualType, boolean isFormulaCell) {
        String msg = "Cannot get quartz " + expectedType + " cellValue from quartz " + actualType + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private static void checkBounds(int cellIndex) {
        SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid columnIndex index (" + cellIndex + ").  Allowable columnIndex range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }

    public XSSFComment getCellComment() {
        return this.getSheet().getCellComment(new CellAddress(this));
    }

    public void setCellComment(Comment comment) {
        if (comment == null) {
            this.removeCellComment();
        } else {
            comment.setAddress(this.getRowIndex(), this.getColumnIndex());
        }
    }

    public void removeCellComment() {
        XSSFComment comment = this.getCellComment();
        if (comment != null) {
            CellAddress ref = new CellAddress(this.getReference());
            XSSFSheet sh = this.getSheet();
            sh.getCommentsTable(false).removeComment(ref);
            sh.getVMLDrawing(false).removeCommentShape(this.getRowIndex(), this.getColumnIndex());
        }

    }

    public XSSFHyperlink getHyperlink() {
        return this.getSheet().getHyperlink(this._row.getRowNum(), this._cellNum);
    }

    public void setHyperlink(Hyperlink hyperlink) {
        if (hyperlink == null) {
            this.removeHyperlink();
        } else {
            XSSFHyperlink link = (XSSFHyperlink) hyperlink;
            link.setCellReference((new CellReference(this._row.getRowNum(), this._cellNum)).formatAsString());
            this.getSheet().addHyperlink(link);
        }
    }

    public void removeHyperlink() {
        this.getSheet().removeHyperlink(this._row.getRowNum(), this._cellNum);
    }

    @Internal
    public CTCell getCTCell() {
        return this._cell;
    }

    @Internal
    public void setCTCell(CTCell cell) {
        this._cell = cell;
    }

    private boolean convertCellValueToBoolean() {
        CellType cellType = this.getCellTypeEnum();
        if (cellType == CellType.FORMULA) {
            cellType = this.getBaseCellType(false);
        }

        switch (cellType) {
            case NUMERIC:
                return Double.parseDouble(this._cell.getV()) != 0.0D;
            case STRING:
                int sstIndex = Integer.parseInt(this._cell.getV());
                XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                String text = rt.getString();
                return Boolean.parseBoolean(text);
            case FORMULA:
            default:
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            case BLANK:
            case ERROR:
                return false;
            case BOOLEAN:
                return "1".equals(this._cell.getV());
        }
    }

    private String convertCellValueToString() {
        CellType cellType = this.getCellTypeEnum();
        switch (cellType) {
            case NUMERIC:
            case ERROR:
                return this._cell.getV();
            case STRING:
                int sstIndex = Integer.parseInt(this._cell.getV());
                XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                return rt.getString();
            case FORMULA:
                cellType = this.getBaseCellType(false);
                String textValue = this._cell.getV();
                switch (cellType) {
                    case NUMERIC:
                    case STRING:
                    case ERROR:
                        return textValue;
                    case FORMULA:
                    case BLANK:
                    default:
                        throw new IllegalStateException("Unexpected formula result type (" + cellType + ")");
                    case BOOLEAN:
                        if ("1".equals(textValue)) {
                            return "TRUE";
                        } else {
                            if ("0".equals(textValue)) {
                                return "FALSE";
                            }

                            throw new IllegalStateException("Unexpected boolean cached formula cellValue '" + textValue + "'.");
                        }
                }
            case BLANK:
                return "";
            case BOOLEAN:
                return "1".equals(this._cell.getV()) ? "TRUE" : "FALSE";
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    public CellRangeAddress getArrayFormulaRange() {
        XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
        if (cell == null) {
            throw new IllegalStateException("Cell " + this.getReference() + " is not part of an array formula.");
        } else {
            String formulaRef = cell._cell.getF().getRef();
            return CellRangeAddress.valueOf(formulaRef);
        }
    }

    public boolean isPartOfArrayFormulaGroup() {
        return this.getSheet().isCellInArrayFormulaContext(this);
    }

    void notifyArrayFormulaChanging(String msg) {
        if (this.isPartOfArrayFormulaGroup()) {
            CellRangeAddress cra = this.getArrayFormulaRange();
            if (cra.getNumberOfCells() > 1) {
                throw new IllegalStateException(msg);
            }

            this.getRow().getSheet().removeArrayFormula(this);
        }

    }

    void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference(this);
        String msg = "Cell " + ref.formatAsString() + " is part of quartz multi-cell array formula. " + "You cannot change part of an array.";
        this.notifyArrayFormulaChanging(msg);
    }
}
