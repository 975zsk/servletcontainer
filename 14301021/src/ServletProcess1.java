
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ServletProcess1 {

    public void process(Request request, Response response) {

        String uri = request.getUri();
        //String[] lists = uri.split("/");
       // String  servletName = lists[2];
         //����web.xml���鿴�Ƿ��д�servlet
        String servletAddr = getServletAddr(uri);
       
        URLClassLoader loader = null;
        
        try {
          // create a URLClassLoader
          URL[] urls = new URL[1];
          URLStreamHandler streamHandler = null;
          System.out.println(Constants.WEB_ROOT);
          File classPath = new File(Constants.WEB_ROOT);
          // the forming of repository is taken from the createClassLoader method in
          // org.apache.catalina.startup.ClassLoaderFactory
          String repository = (new URL("file", null, classPath.getCanonicalPath() )).toString() ;
          // the code for forming the URL is taken from the addRepository method in
          // org.apache.catalina.loader.StandardClassLoader class.
          urls[0] = new URL(null, repository, streamHandler);
          loader = new URLClassLoader(urls);
        }
        catch (IOException e) {
          System.out.println(e.toString() );
        }
        Class myClass = null;
        try {
          myClass = loader.loadClass(servletAddr);
        }
        catch (ClassNotFoundException e) {
          System.out.println(e.toString());
        }
     
        Servlet servlet = null;
     
       //��request��response��������࣬��ȫ�Կ��ǣ���ֹ�û���servlet��ֱ�ӽ�ServletRequest��ServletResponse����ת��ΪRequest��Response���ͣ�
        //��ֱ�ӵ������ڲ���public��������ΪRequestFacade��ResponseFacade�ﲻ����parse��sendStaticResource�ȷ�����
        //RequestFacade requestFacade = new RequestFacade(request);
       // ResponseFacade responseFacade = new ResponseFacade(response);

        try {
          servlet = (Servlet) myClass.newInstance();
          servlet.service((ServletRequest) request, (ServletResponse) response);
        }
        catch (Exception e) {
          System.out.println(e.toString());
        }
        catch (Throwable e) {
          System.out.println(e.toString());
        }
    }
    
    //����xml
    public String getServletAddr(String servletUrl){
    	String servletAddr =null;
    	
    	File f= new File("web.xml");
    	DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
    	
    	try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc =db.parse(f);
			
			NodeList servletList = doc.getElementsByTagName("servlet");		
            NodeList mappinglist = doc.getElementsByTagName("servlet-mapping");	
			for(int i=0;i<servletList.getLength();i++){
				Element servlet = (Element) servletList.item(i);
				Element mapping = (Element) mappinglist.item(i);
	
				String url = mapping.getElementsByTagName("url-pattern").item(0).getFirstChild().getNodeValue();		
				String servlet_class = servlet.getElementsByTagName("servlet-class").item(0).getFirstChild().getNodeValue();
				
				if(url.endsWith(servletUrl)){
					servletAddr =servlet_class;
				}
				
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return  servletAddr;
    }
}