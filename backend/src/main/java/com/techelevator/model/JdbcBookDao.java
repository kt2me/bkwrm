package com.techelevator.model;

import java.io.Console;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;

@Component
public class JdbcBookDao implements BookDao {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public JdbcBookDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Book> getAllBooks() {
		List<Book> book = new ArrayList<Book>();
		String sqlGetAllBooks = "SELECT * FROM books";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllBooks);
		Book theBook;
		while (results.next()) {
			theBook = mapRowToBook(results);
			System.out.println(theBook);
			book.add(theBook);
		}
		return book;
	}
	
	@Override
	public List<Book> getAllBooksFromReadingList(int userId) {
		List<Book> books = new ArrayList<Book>();
		String result = "SELECT books.book_id, books.title, books.author, books.genre, books.description, books.publish_date, books.date_added, books.img_url, books.isbn, user_books.completed FROM books JOIN user_books ON books.book_id = user_books.book_id WHERE user_books.user_id = ?" ;
		SqlRowSet results = jdbcTemplate.queryForRowSet(result, userId);
		Book theBook;
		while (results.next()) {
			theBook = mapRowToReadingListBook(results);
			books.add(theBook);
		}
		return books;
	}
	
	@Override
	public void saveBookToReadingList(Book book, int userId) {
		int id = getNextId();
		String sqlSave = "INSERT INTO user_books (user_id, book_id) VALUES (?,?)";	
		jdbcTemplate.update(sqlSave, userId, book.getId());
		book.setId(id);
	}
	
	@Override
	public void removeBookFromReadingList(Book book, int userId) {
		String sqlRemoveFromReadingList = "DELETE FROM user_books WHERE book_id = ? AND user_id = ?";
		jdbcTemplate.update(sqlRemoveFromReadingList, book.getId(), userId);
	}
	
	@Override
	public Book getBookById(int bookId) {
		Book book = new Book();
		String getBook = "SELECT * FROM books WHERE book_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getBook, bookId);
		
		if(result.next()) {
		book = mapRowToBook(result);
		}
		return book;	
	}

	private Book mapRowToBook(SqlRowSet results) {

		Book theBook;

		theBook = new Book();
		theBook.setId(results.getInt("book_id"));
		theBook.setAuthor(results.getString("author"));
		theBook.setDescription(results.getString("description"));
		theBook.setGenre(results.getString("genre"));
		theBook.setTitle(results.getString("title"));
		theBook.setPublishDate(results.getDate("publish_date"));
		theBook.setDateAdded(results.getDate("date_added"));
		theBook.setImgUrl(results.getString("img_url"));
		theBook.setIsbn(results.getString("isbn"));

		return theBook;
	}
	
	private Book mapRowToReadingListBook(SqlRowSet results) {

		Book theBook;

		theBook = new Book();
		theBook.setId(results.getInt("book_id"));
		theBook.setAuthor(results.getString("author"));
		theBook.setDescription(results.getString("description"));
		theBook.setGenre(results.getString("genre"));
		theBook.setTitle(results.getString("title"));
		theBook.setPublishDate(results.getDate("publish_date"));
		theBook.setDateAdded(results.getDate("date_added"));
		theBook.setImgUrl(results.getString("img_url"));
		theBook.setIsbn(results.getString("isbn"));
		theBook.setCompleted(results.getBoolean("completed"));
		return theBook;
	}

	
	@Override
	public void save(Book book) {		
		int id = getNextId();
		String sqlSave = "INSERT INTO books (book_id,title,author,genre,description,publish_date,date_added,img_url,isbn ) " + 
				"VALUES (?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sqlSave, id, book.getTitle(), book.getAuthor(),book.getGenre(),book.getDescription(),book.getPublishDate(),
				book.getDateAdded(),book.getImgUrl(), book.getIsbn());
		book.setId(id);
	}

	@Override
	public void updateReadingList (Book book, int userId) {
		String sqlCompleted = "UPDATE user_books SET completed = NOT completed WHERE book_id = ? AND user_id = ?";
		jdbcTemplate.update(sqlCompleted, book.getId(), userId);
	}
	
	private int getNextId() {

		String sqlSelectNextId = "SELECT nextval('books_book_id_seq')";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectNextId);
		int id = 0;
		if (results.next()) {
			id = results.getInt(1);
		} else {
			throw new RuntimeException("Something strange happened, unable to select next forum post id from sequence");
		}
		return id;
	}

	

}
