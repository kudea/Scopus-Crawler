package crawl;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class WriteCSV {

	
	
	public void writeData(ScopusBasicInfo bi) throws IOException {
		
		String csv = "scopus_basic_info.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

		String id = String.valueOf(bi.paperID);
		String title = bi.title;
		String year = String.valueOf(bi.year);
		String sourceTitle = bi.sourceTitle;
		String volume = bi.volume; 
		String issue = bi.issue;
		String pageStart = bi.pageStart;
		String pageEnd = bi.pageEnd;
		String citeBy = String.valueOf(bi.citeBy);
		String DOI = bi.DOI;
		String publisher = bi.publisher;
		String conferenceName = bi.conferenceName;
		String conferenceCode = bi.conferenceCode;
		String ISSN = bi.ISSN;
		String ISBN = bi.ISBN;
		String documentType = bi.documentType;
		String source = bi.source;
		String EID = bi.EID;
		String citeScore = String.valueOf(bi.citeScore);
		String downloadTime = bi.downloadTime;
		
		
		String[] output = {id, title, year, sourceTitle, volume, issue, pageStart, pageEnd, citeBy, DOI, publisher, conferenceName,
				conferenceCode, ISSN, ISBN, documentType, source, EID, citeScore, downloadTime};
		
		writer.writeNext(output);

		writer.close();
	}

}