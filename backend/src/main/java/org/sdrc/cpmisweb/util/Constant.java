package org.sdrc.cpmisweb.util;

public class Constant {

	public static final String SUBMISSION_PDF_OUTPUT_PATH = "output.path.submission.pdf";
	public static final String CPMISWEB_ROOT_PATH = "cpmis.root.path";
	public static final String RAWDATA_REPORT_PATH = "rawdata.report.path";
	public static final String DOMAIN_NAME = "domain.name";
	public static final String OUTPUT_PATH_DASHBOARD_PDF = "output.path.dashboard.pdf";
	public static final String OUTPUT_PATH_PDF_LINECHART = "output.path.pdf.linechart";
	
	public static final Integer STATE_LEVEL_USER_TYPE_ID = 11;
    public static final Integer DCPU_USER_TYPE_ID = 9;
    
    public static final Integer PERIODICITY_FOR_MONTH = 1;
    public static final Integer PERIODICITY_FOR_QUARTER = 2;
    
    public static final Integer[] UNITS_TO_BE_SHOWN_IN_DASHBOARD = {1};
    public static class Slices{
		public static String FIRST_SLICE = "firstslices";
		public static String SECOND_SLICE = "secondslices";
		public static String THIRD_SLICE = "thirdslices";
		public static String FOUTRH_SLICE = "fourthslices";
		public static String FIFTH_SLICE = "fifthslices";
		public static String SIXTH_SLICE = "sixthslices";
	}
}
