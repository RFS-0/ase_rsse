package ase.rsse.apirec.transactions;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import ase.rsse.utilities.IoUtility;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.completionevents.ICompletionEvent;

public class TransactionCreator {
	
	public static ArrayList<String> EXCLUDED_FILES = getExcludedFiles();
	
	public static void main(String[] args) {
		// find all zips with event data
		Set<String> allEventData = IoUtility.findAllFiles(ITransactionConstants.EVENTS_DIRECTORY, s -> s.endsWith(".zip"));
		System.out.println("Number of event data zips found: " + allEventData.size());
		
		System.out.println("Starting creation of transactions...");
		
		for (String eventData: allEventData) {
			if (!EXCLUDED_FILES.contains(eventData)) {
				System.out.println(eventData);
				List<ICompletionEvent> events = IoUtility.readEvent(eventData);
				HashMap<String, ArrayList<CompletionEvent>> fileToEvents = prepareCompletionEvents(events);
				processCompletionEvents(fileToEvents);
			}
		}
	}

	private static void processCompletionEvents(HashMap<String, ArrayList<CompletionEvent>> fileToEvents) {
		// process each file
		int numberOfFilesToProcess = fileToEvents.entrySet().size();
		int numberOfFilesProcessed = 0;
		System.out.println("Number of files to process: " +  numberOfFilesToProcess);
		System.out.println("Processing files...");
		for (String key: fileToEvents.keySet()) {
			
			ArrayList<CompletionEvent> completionEvents = fileToEvents.get(key);
			List<CompletionEvent> sortedCompletionEvents = completionEvents.stream()
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(CompletionEvent::getTriggeredAt, Comparator.reverseOrder()))
					.collect(Collectors.toList());
			
			// process successive pairs
			for (int i = 0; i < sortedCompletionEvents.size() - 1; i++) {
				String fileName = TransactionUtility.clean(sortedCompletionEvents.get(i+1).getContext().getSST().getEnclosingType().getFullName());
				File file = new File(ITransactionConstants.TRANSACTION_DIRECTORY + fileName);
				if (!file.exists()) {
					System.out.println("Creating transaction...");
					TransactionUtility.createTransaction(sortedCompletionEvents.get(i), sortedCompletionEvents.get(i+1), i);
				} else {
					System.out.println("Transaction already exsisted...");
				}
			}

			
			numberOfFilesProcessed += 1;
			System.out.println("Number of files processed: " + numberOfFilesProcessed + " / " + numberOfFilesToProcess);
		}
	}

	private static HashMap<String, ArrayList<CompletionEvent>> prepareCompletionEvents(List<ICompletionEvent> events) {
		System.out.println("Preparing completion events...");
		HashMap<String, ArrayList<CompletionEvent>> fileToEvents = new HashMap<>();
		for (ICompletionEvent event: events) {
			CompletionEvent e = (CompletionEvent) event;
			String fullName = e.getContext().getSST().getEnclosingType().getFullName();
			if (fileToEvents.containsKey(fullName)) {
				fileToEvents.get(fullName).add(e);
			} else {
				ArrayList<CompletionEvent> evts = new ArrayList<>();
				evts.add(e);
				fileToEvents.put(fullName, evts);
			}
		}
		return fileToEvents;
	}
	
	public static ArrayList<String> getExcludedFiles() {
		ArrayList<String> excluded = new ArrayList<>();
		excluded.add("C:\\workspaces\\ase_rsse\\apirec\\Events-170301-2\\2016-05-09\\1.zip");
		return excluded;
	}
}
