package crawl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ScopusBasicInfo {

	int paperID;
	String title;
	String year;
	String sourceTitle;
	String volume;
	String issue;
	String pageStart;
	String pageEnd;
	String citeBy;
	String DOI;
	String publisher;
	String conferenceName;
	String conferenceCode;
	String ISSN;
	String ISBN;
	String documentType = "";
	String source = "SCOPUS";
	String EID;
	String citeScore;
	String downloadTime = "2019-4-17";

	WriteCSV wc = new WriteCSV();
	
	ScopusBasicInfo(int paperID) {
		this.paperID = paperID;
	}
	
	void crawl (String paperUrl) {
		try {
			Document doc = Jsoup.connect(paperUrl).timeout(500000).get();
			getTitle(doc);
			getJournalInfo(doc);
			getSourceTitle(doc);
			getCiteBy(doc);
			getDOI(doc);
			getDocumentInfo(doc);
			getConferenceInfo(doc);
			getCitationInfo(doc);
			getEID(paperUrl);
			getCiteScore();
			
			printInfo();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// title : return type just for testing
	private String getTitle(Document doc) {
		Elements elements = doc.select("h2.h3");
		elements.select("span").remove(); // this is attribute "documentType" we want //TODO <p>?
		String paperTitle = elements.text();
		title = paperTitle;
		
		return paperTitle;
	}
	
	// year volume issue pageStart pageEnd
	private void getJournalInfo(Document doc) {
		Elements elements = doc.select("#journalInfo");
		String dirtyInfo = elements.text();
		String[] array = dirtyInfo.split(",");
		for(int i = 0; i < array.length ; i++) {
			if(array[i].toUpperCase().contains("VOLUME")) {
				volume = array[i].substring(7); // delete "Volume " 7 chars
			}
			else if(array[i].toUpperCase().contains("ISSUE")) {
				issue = array[i].substring(7); // delete " Issue " 7 chars
			}
			else if(array[i].toUpperCase().contains("PAGES")) {
				String[] pages = array[i].substring(7).split("-");
				if(pages.length < 2 && i+2 < array.length) {
					String dirtyPage = array[i] + array[i+1] + array[i+2];
					
					pages = dirtyPage.substring(7).split("-");
				}
				if(pages.length == 2) {
					pageStart = pages[0];
					pageEnd = pages[1];
				}
			}
			else {
				String[] months = {"January" , "February" , "March" , "April" , "May" , "June" , 
						"July" , "August" , "September" , "October" , "November" , "December"};
				for(String month : months) {
					if(array[i].contains(month)) {
						year = array[i].substring(array[i].length()-4);
					}
				}
			}
		} // end for
		//System.out.println("Year=" + year + "\nVolume=" + volume + "\nIssue=" + issue + "\nPageStart=" + pageStart + "\nPageEnd=" + pageEnd + "\n--------------------");
	}
	
	// sourceTitle
	private void getSourceTitle(Document doc) {
		Elements elements = doc.select("#publicationTitle span.anchorText");
		sourceTitle = elements.text();
	}
	
	// citeBy
	private void getCiteBy(Document doc) {
		Elements elements = doc.select("#citeCnt");
		citeBy = elements.text();
	}
	
	// DOI
	private void getDOI(Document doc) {
		
		Elements elements = doc.select("#recordDOI");
		DOI = elements.text();
		//System.out.println(DOI);
	}
	
	// documentType publisher : actually DOI can get from this function
	private void getDocumentInfo(Document doc) {
		
		Elements elements = doc.select("#documentInfo li");
		for(Element e : elements) {
			if(e.text().toUpperCase().contains("DOCUMENT TYPE")) {
				documentType = e.text().substring(15); // delete "Document Type :" 15 chars
				//System.out.println(documentType);
			}
			if (e.text().toUpperCase().contains("PUBLISHER")) {
				publisher = e.text().substring(11); // delete "Publisher :" 11 chars
			}
		}
	}
	
	// conferenceName conferenceCode
	private void getConferenceInfo(Document doc) {
		if(this.documentType.equals("Conference Paper")) { //list-group-item
			Elements elements = doc.select("span.list-group-item");
			for(Element e : elements) {
				if(e.text().contains(";")) {
					String[] arrayData = e.text().split(";");
					conferenceName = arrayData[0];
					for(int i = 1; i < arrayData.length; i++) { // from 1 is to delete the first is conferenceName
						if(arrayData[i].toUpperCase().contains("CODE")) {
							conferenceCode = arrayData[i].substring(6);
						}
					}
				}
			}
			//System.out.println(conferenceName +"\n"+ conferenceCode);
		}
	}
	
	// ISSN ISBN : this function name is named by the tag name we crawl
	private void getCitationInfo(Document doc) {
		Elements elements = doc.select("#citationInfo li");
		for(Element e : elements) {
			if(e.text().toUpperCase().contains("ISSN")) {
				ISSN = e.text().substring(6); // delete "ISSN: " 5 chars
				//System.out.println(documentType);
			}
			if (e.text().toUpperCase().contains("ISBN")) {
				ISBN = e.text().substring(6); // delete "ISBN: " 5 chars
			}
		}
		//System.out.println(ISSN +"\n"+ ISBN);
	}
	
	// EID cut the url
	private void getEID(String paperUrl) {
		String[] dirty = paperUrl.split("&");
		EID = dirty[0].substring(45);
	}
	
	// citeScore link to journal and get this
	private void getCiteScore() {
		
		String apiKey = "0664712d33abb96e29dcb74cc664f75b";
		if(ISSN != null) {

			String uri = "https://api.elsevier.com/content/serial/title/issn/"+ISSN+"?apiKey="+apiKey;

			URL url;
			try {
				url = new URL(uri);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/xml");

				InputStream xml = connection.getInputStream();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				org.w3c.dom.Document doc = db.parse(xml);
				doc.getElementsByTagName("citeScoreCurrentMetric");
				
				NodeList nList = doc.getElementsByTagName("citeScoreCurrentMetric");
				if(nList.getLength()!=0) {
					citeScore = nList.item(0).getTextContent();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
	}
	
	// print the info and write to CSV
	private void printInfo() {
		System.out.println("ID :\t\t" + paperID);
		/**System.out.println("Title :\t\t" + title);
		System.out.println("Year :\t\t" + year);
		System.out.println("SourceTitle :\t" + sourceTitle);
		System.out.println("Volume :\t" + volume);
		System.out.println("Issue :\t\t" + issue);
		System.out.println("PageStart :\t" + pageStart);
		System.out.println("PageEnd :\t" + pageEnd);
		System.out.println("CiteBy :\t" + citeBy);
		System.out.println("DOI :\t\t" + DOI);
		System.out.println("Publisher :\t" + publisher);
		System.out.println("ConferenceName :" + conferenceName);
		System.out.println("ConferenceCode :" + conferenceCode);
		System.out.println("ISSN :\t\t" + ISSN);
		System.out.println("ISBN :\t\t" + ISBN);
		System.out.println("DocumentType :\t" + documentType);
		System.out.println("Source :\t" + source);
		System.out.println("EID :\t\t" + EID);
		System.out.println("CiteScore :\t" + citeScore);
		System.out.println("DownloadTime :\t" + downloadTime); // ???
		System.out.println("-----------------------------------------------------");**/
		
		try {
			wc.writeData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
