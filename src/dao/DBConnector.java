package dao;

import util.DButil;
import objects.Book;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import objects.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnector {
	
	DButil dbUtil = new DButil();
	
	public User checkCredentials(String email, String password)
	{
		User user = new User();
		try {
            Connection conn = dbUtil.getConnection();
            String query = "SELECT user_id, user_type, user_email, user_name, user_password FROM users WHERE email = "+email+";";
            
            Statement st = conn.createStatement();
           
            ResultSet rs = st.executeQuery(query);
            
            
            while (rs.next()) {
            	String em = rs.getString("user_email");
            	String pw = rs.getString("user_password");
            	
                if (em.equals(email)&& pw.equals(password) ) {
                
                    user.setMemId(rs.getString("user_id"));
                    user.setEmail(em);
                    user.setPassword(pw);
                    user.setType(rs.getString("user_type"));
                    user.setName(rs.getString("user_name"));
                    
                	}
                }
            }
            
         catch (SQLException e) {
            System.err.println("Got an exception in checkcredentials in dbconnector");
            System.err.println(e.getMessage());
        }
		return user;
	}
	
	List<Book> browseBooks() {
		
		Connection conn;
		List<Book> books = new ArrayList<Book>();
		try {
			conn = dbUtil.getConnection();
			 String query = "SELECT * FROM books";		        
		      Statement st = conn.createStatement();	     	      
		      ResultSet bookSet = st.executeQuery(query);		      
		      while(bookSet.next()) {
		    	  
		    	  Book book = new Book();
		    	  
		    	  book.setTitle(bookSet.getString("book_title"));
		    	  book.setAuthor(bookSet.getString("book_author"));
		    	  book.setAvailable(bookSet.getInt("book_available"));
		    	  book.setQuantity(bookSet.getInt("book_quantity"));
		    	  book.setGenre(bookSet.getString("book_genre"));
		    	  book.setid(bookSet.getString("book_id"));
		    	  book.setISBN(bookSet.getString("book_ISBN"));
		    	  book.setPublisher(bookSet.getString("book_publisher"));
		    	  books.add(book);
		    	  
		      }
		      
		   	      
		      
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while getting books");
			e.printStackTrace();
		}
		
		return books;
		
	}
	
	
	List<Book> searchBook(String title,String author,String genre, int ISBN, String publisher){
		
		Connection conn;
		List<Book> books = browseBooks();
		List<Book> matchingBooks= new ArrayList<Book>();
		int size = books.size();
		
		for (int i=0;i<size;i++) {
			
			Book book = books.get(i);
			
			if((book.getTitle().contains(title) && (!title.equals(""))) && (book.getAuthor().contains(author) && (!author.equals("")))&& (book.getGenre().contains(genre) && (!genre.equals(""))) && (book.getPublisher().contains(publisher) && (!publisher.equals(""))) && (book.getISBN().contains(Integer.toString(ISBN)) && (ISBN!=0))) {
				
				matchingBooks.add(book);
			}
		}
		
		return matchingBooks;
	}
		
	
	
	boolean borrowBook(int user_id, int bookId)
	{
	
		
		
		Connection connection;
		int i = 0;
		try {
			connection = dbUtil.getConnection();
			PreparedStatement ps;
			String query="SELECT COUNT(user_id) FROM currentlyIssued";			
			 Statement st = connection.createStatement();         
	         ResultSet rs = st.executeQuery(query);
	         int noOfBooks=rs.getInt(0);
	         
	         if(noOfBooks>=2) {
	        	 System.out.println("Only two books bro");
	         }
	         
			ps = connection.prepareStatement("UPDATE books SET book_available = (book_available - 1) WHERE bookId = ?");
			ps.setInt(1, bookId);
			ps.executeUpdate();
			ps = connection.prepareStatement("DELETE from waitlist WHERE user_id=? AND book_id=?;");
			ps.setInt(1, user_id);
			ps.setInt(2, bookId);
			ps.executeUpdate();
	        ps = connection.prepareStatement("INSERT INTO currentlyIssued (book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?);");
            ps.setInt(1, bookId);
	        ps.setInt(2, user_id);
	        LocalDate idate = LocalDate.now();
	        LocalDate ddate = idate.plusDays(14);
	        ps.setDate(3, java.sql.Date.valueOf(idate));
	        ps.setDate(4, java.sql.Date.valueOf(ddate));
	        
	        i = ps.executeUpdate();
	        
	        
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in borrowbook in dbconnector");
		}
		if (i == 1) 
            return true;
         else 
            return false;
	}
	
	double returnBook(int user_id, int bookId) 
	{
		Connection connection;
		int i = 0;
		try {
			connection = dbUtil.getConnection();
			PreparedStatement ps;
			ps = connection.prepareStatement("UPDATE books SET available_copies = (available_copies + 1) WHERE bookId = ?");
			ps.setInt(1, bookId);
			ps.executeUpdate();
	        ps = connection.prepareStatement("DELETE FROM currentlyIssued WHERE user_id=? AND bookId=?;");
            ps.setInt(1, user_id);
	        ps.setInt(2, bookId);
	        
	        ps = connection.prepareStatement("INSERT INTO lms_db.past_issues (bookId, user_id, num) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE num = num + 1;");
	        ps.setInt(1, bookId);
	        ps.setInt(2, user_id);
	        i = ps.executeUpdate();
	        
	        
	        
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in returnbook in dbconnector");
		}
		return calcFine(user_id, bookId);
	}
	
	boolean getCurrentlyIssued(String book_id, String user_id)
	{
		Connection conn;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("SELECT book_id, user_Id, issue_date, due_date FROM currentlyIssued WHERE book_id = ? AND user_id = ?;");
	        ps.setString(1, book_id);
	        ps.setString(2, user_id);
	        ResultSet rs = ps.executeQuery();
	        while(rs.next())
	        {
	        	return true;
	        }
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in currentlyissued in dbconnector");
		}
		return false;
        
	}
	boolean deleteBook(int bookId)
	{
		Connection conn; int x=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("DELETE from books WHERE book_id = ?;");
	        ps.setInt(1, bookId);
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in deletebook in dbconnector");
		}
		if (x == 1) 
            return true;
         else 
            return false;
	}
	
	double deleteMember(int user_id)
	{
		Connection conn; int x=0;
		double fine=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("DELETE from lms_db.member_list WHERE user_id = ?;");
	        ps.setInt(1, user_id);
	        x = ps.executeUpdate();
	        ps = conn.prepareStatement("SELECT book_Id, mem_Id FROM currentlyIssued WHERE user_id = ?;");
	        ResultSet rs = ps.executeQuery();
	        //int bookId = rs.getInt(1);
	        if(rs.next()) {
	        	PreparedStatement ps2 = conn.prepareStatement("UPDATE books SET available_copies = (available_copies + 1) WHERE bookId = ?;");
	        	ps2.setInt(1, rs.getInt(1));
	        	ps2.executeUpdate();
	            fine = calcFine(user_id, rs.getInt(1));
	        }
	        ps = conn.prepareStatement("DELETE from currentlyIssued WHERE user_id = ?;");
	        ps.setInt(1, user_id);
	        x = ps.executeUpdate();
	       
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in deletemember in dbconnector");
		}
		 return fine;
	}
	
	public boolean addBook(int bookId, String title, String genre, String author,String publisher, String ISBN, int total_copies)
	{
		Connection conn; int x=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("INSERT into books (book_id, book_ISBN, book_title, book_author,book_publisher,book_genre,book_quantity,book_available) values (NULL, ?, ?, ?, ?, ?, ?, 0);");
	        //generate an ID
			ps.setString(2, ISBN);
	        ps.setString(3, title);
	        ps.setString(4, author);
	        ps.setString(5, publisher);
	        ps.setString(6, genre);
	        ps.setInt(7, total_copies);
	        ps.setInt(8, total_copies);
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in addbook in dbconnector");
		}
		if (x == 1) 
            return true;
         else 
            return false;
	}
	
	public boolean addMember(String type, String email, String name, String password)
	{
		Connection conn; int x=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("INSERT into member_list (user_id, user_type, user_email, user_name, user_password) values (0, ?, ?, ?, ?);");
	        ps.setString(2, type);
	        ps.setString(3, email);
	        ps.setString(4, name);
	        ps.setString(5, password);
	       
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in addmember in dbconnector");
		}
		if (x == 1) 
            return true;
         else 
            return false;
	}
	
	boolean editBook(int user_id, String type, String email, String name, String password)
	{
		Connection conn; int x=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE book_list SET user_type=?, user_email=?, user_name=?, user_password=? WHERE user_id=?;");
	        ps.setString(1, type);
	        ps.setString(2, email);
	        ps.setString(3, name);
	        ps.setString(4, password);
	        ps.setInt(5, user_id);
	        
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in editbook in dbconnector");
		}
		if (x == 1) 
            return true;
         else 
            return false;
	}
	
	boolean editUser(String user_id,String user_type,String user_name, String user_email, String user_password)
	{
		Connection conn; int x=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE users SET user_type=?,user_name=?,user_email=?,user_password=?  WHERE user_id=?;");
	        ps.setString(1, user_type);
	        ps.setString(2, user_name);
	        ps.setString(3, user_email);
	        ps.setString(4, user_password);
	        ps.setString(5, user_id);
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in editmember in dbconnector");
		}
		if (x == 1) 
            return true;
         else 
            return false;
	}
	
	double calcFine(int user_id, int bookId) {
		Connection conn; int x=0;
		double fine=0;
		try {
			conn = dbUtil.getConnection();
			PreparedStatement ps;
			ps = conn.prepareStatement("SELECT issue_date, due_date FROM currentlyIssued WHERE user_id =? AND bookId=?;");
	        ps.setInt(1, user_id);
	        ps.setInt(2, bookId);
	        ResultSet rs = ps.executeQuery();
	        while(rs.next()) {
	        	LocalDate idate = rs.getDate(3).toLocalDate();
	        	LocalDate ddate = rs.getDate(4).toLocalDate();
	        	if(ddate.isAfter(idate)) {
	        	Period period = Period.between(ddate, idate);
	        	int daysElapsed = period.getDays();
	        	if(Math.abs(daysElapsed)>0)
	        	   fine = daysElapsed*20;
	        	}
	        }
	       
	        x = ps.executeUpdate();
	        
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Got an exception in calcfine in dbconnector");
		}
		return fine;
		
	}
	
	

}
        
	


