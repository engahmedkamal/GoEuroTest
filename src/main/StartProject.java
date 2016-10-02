package main;

import java.util.Scanner;

import control.GetDataFromApi;

public class StartProject {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hello this is GoEuro Search app");
		System.out.println("Enter the city name you searching for :");
		String cityName = scanner.next();
		scanner.close();
		GetDataFromApi runApp=new GetDataFromApi();
		runApp.execute(cityName);
	}

}
