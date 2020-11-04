package scopus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		FB.login(function(response) {
			  console.log(response);
			}, {scope: 'user_birthday'});

	}

}
