package com.example.tp_bibliotheque.Tests;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Book;
import com.example.tp_bibliotheque.Objects.HasWritten;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

//CLASSE DE TESTS DE METHODES RELATIVES AUX LIVRES

public class BookTests {
    //*****************ATTRIBUTS*****************//

    //Livre Hunger Games, 1er livre de la BDD
    private final Book hungerGames;

    //Livre Harry Potter, 2e livre de la BDD
    private final Book harryPotter;


    //*****************METHODES*****************//

    //Constructeur de classe
    public BookTests() throws SQLException, IOException {
        //Pour pouvoir se connecter à la BDD
        MainApplication main = new MainApplication();

        //NE PAS OUBLIER DE COMMENTER "coverImg = new Image(coverImgURL)" DANS BOOK
        hungerGames = Book.getBook(1);
        harryPotter = Book.getBook(2);
    }

    //Test de la méthode cleanGenres()
    @org.junit.Test
    public void cleanGenres() throws SQLException {
        System.out.println("Testing Genre Cleaning");

        //Genres du livre Hunger Games
        assertArrayEquals(new String[]{"Young Adult", "Fiction", "Dystopia", "Fantasy", "Science Fiction", "Romance", "Adventure", "Teen", "Post Apocalyptic", "Action"}, hungerGames.getGenres());
        //Nombre de genres du livre Harry Potter
        assertEquals(10, harryPotter.getGenres().length);

        System.out.println("Test passed :)");
    }

    //Test de la méthode getBookFromSearch
    @org.junit.Test
    public void getBookFromSearch() throws SQLException {
        //Livres retournés après la recherche "the hunger games"
        Vector<Book> searchedBooks = Book.getBookFromSearch("the hunger games");

        //Livres retournés après une recherche vide
        Vector<Book> notSearch = Book.getBookFromSearch("");

        System.out.println("Testing Books Searching");

        //Taille de la recherche vide
        assertEquals(10, notSearch.size());
        //Taille de la recherche "the hunger games"
        assertEquals(6, searchedBooks.size());
        //Premier livre de la recherche "the hunger games"
        assertEquals("The Hunger Games", searchedBooks.get(0).getTitle());

        System.out.println("Test passed :)");
    }

    //Test de la méthode getCredits()
    @org.junit.Test
    public void getCredits() throws SQLException {
        //Crédits du livre Harry Potter
        Vector<HasWritten> harryPotterCredits = harryPotter.getCredits();

        System.out.println("Testing Books Credits");

        //Nombres d'auteurs ayant participés aux livres
        assertEquals(2, harryPotterCredits.size());
        //Prénom du 1er auteur
        assertEquals("J.K.", harryPotterCredits.get(0).getAuthor().getName());
        //Nom du 1er auteur
        assertEquals("Rowling", harryPotterCredits.get(0).getAuthor().getLastName());
        //Rôle du 2e auteur
        assertEquals("(Illustrator)", harryPotterCredits.get(1).getRole());

        System.out.println("Test passed :)");
    }
}
