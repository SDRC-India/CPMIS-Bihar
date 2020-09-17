package org.sdrc.cpmisweb.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sdrc.usermgmt.model.UserModel;
import org.springframework.util.ResourceUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

/** Inner class to add a header and a footer. */
@Slf4j
public class HeaderFooter extends PdfPageEventHelper {	
	/** Alternating phrase for the header. */
	Phrase[] header = new Phrase[2];
	/** Current page number (will be reset for every chapter). */
	int pagenumber;
	private String domainName;
	private String userName;
	private String timeperiod;
	
	public HeaderFooter(String domainName, UserModel user, String timeperiod){
		this.domainName = domainName;
		this.userName = user.getUsername();
		this.timeperiod = timeperiod;
	}

	/**
	 * Increase the page number.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onStartPage(PdfWriter writer, Document document) {
		
		
		Image image;
		try {
			document.setMargins(36, 36, 60, 36);

			PdfContentByte cb = writer.getDirectContentUnder();
			String userDetails = "User: "+userName+", Timeperiod: "+timeperiod;
			image = Image.getInstance(ResourceUtils.getFile("classpath:images"+File.separator+"cpmis-submission-pdf-hrader.png").getAbsolutePath());
			Font font = new Font();
			font.setSize(25f);
			document.add(getWatermarkedImage(cb, image, userDetails, document, font, 0, 805));
			document.add(Chunk.NEWLINE);
			pagenumber++;
		} catch (Exception e) {
			log.error("Error inside onStartPage() of HeaderFooter.java", e);
		}
		
	}
	
	public Image getWatermarkedImage(PdfContentByte cb, Image img, String watermark, Document document, Font font, float x, float y) throws DocumentException {
	    float width = img.getScaledWidth();
	    float height = img.getScaledHeight();
	    PdfTemplate template = cb.createTemplate(width, height);
	    template.addImage(img, width, 0, 0, height, 0, 0);
	   
	    ColumnText.showTextAligned(template, Element.ALIGN_CENTER, new Phrase(watermark, font), width/2, height/3, 0);
	    Image image = Image.getInstance(template);
	    float scaler = ((document.getPageSize().getWidth()) / image.getWidth()) * 100;

		image.scalePercent(scaler);
	    image.setAbsolutePosition(x, y);
	    return image;
	    
	    
	}

	/**
	 * Adds the header and the footer.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			Image image;
			PdfContentByte cb = writer.getDirectContentUnder();
			String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
			String domain = "Page - "+pagenumber+", Printed on : "+date+", from "+domainName;
			image = Image.getInstance(ResourceUtils.getFile("classpath:images"+File.separator+"cpmis-submission-pdf-footer.png").getAbsolutePath());
			Font font = new Font();
			font.setSize(20f);
			font.setColor(255,255,255);
			document.add(getWatermarkedImage(cb, image, domain, document, font, 0, 0));
		} catch (Exception e) {
			log.error("Error inside onEndPage() of HeaderFooter.java", e);
		}
		
	}
}