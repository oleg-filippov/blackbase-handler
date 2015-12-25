package net.filippov.uniastrum.blackbase_handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.filippov.uniastrum.blackbase_handler.converter.Record;
import net.filippov.uniastrum.blackbase_handler.converter.TerroristParser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TerroristParserTest {
	
	private static Map<TerroristParser, List<Record>> assertMap;
	
	@BeforeClass
	public static void init() {
		assertMap = new HashMap<TerroristParser, List<Record>>();
		assertMap.put(
				new TerroristParser(
						"АБДУРАХМАНОВ АБДУРАХМАН АБДУЛАТИПОВИЧ",
						"13.04.65",
						"9607",
						"003207"),
				Arrays.asList(new Record(new String[]{
						"АБДУРАХМАНОВ",
						"АБДУРАХМАН",
						"АБДУЛАТИПОВИЧ"},
						"13.04.65",
						"9607",
						"003207"))
		);
		assertMap.put(
				new TerroristParser(
						"АСА- ДУЛЛИН  СУЛЕЙМАН ДИНАРТОВИЧ   (АСАДУЛЛИН ДМИТРИЙ ДИНАРТОВИЧ; АХСАДУЛИН СУ- ЛЕЙМАН ДИЛАРТОВИЧ)",
						"13.04.65",
						"32434",
						"123456"),
				Arrays.asList(
						new Record(new String[]{
								"АСА-ДУЛЛИН",
								"СУЛЕЙМАН",
								"ДИНАРТОВИЧ"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"АСАДУЛЛИН",
								"ДМИТРИЙ",
								"ДИНАРТОВИЧ"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"АХСАДУЛИН",
								"СУ-ЛЕЙМАН",
								"ДИЛАРТОВИЧ"},
								"13.04.65",
								"",
								""))
		);
		assertMap.put(
				new TerroristParser(
						"НАТОВА МИЛАНА ХУСЕНОВНА (НАТОВА МИЛ -АННА  (МИЛА - НАНА) ХУСЕНОВНА; ХАПЦЕВА МИЛАННА (МИЛАНА) ХУСЕНОВНА)",
						"13.04.65",
						"32р43",
						"123456"),
				Arrays.asList(
						new Record(new String[]{
								"НАТОВА",
								"МИЛАНА",
								"ХУСЕНОВНА"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"НАТОВА",
								"МИЛ-АННА",
								"ХУСЕНОВНА"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"НАТОВА",
								"МИЛА-НАНА",
								"ХУСЕНОВНА"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"ХАПЦЕВА",
								"МИЛАННА",
								"ХУСЕНОВНА"},
								"13.04.65",
								"",
								""),
						new Record(new String[]{
								"ХАПЦЕВА",
								"МИЛАНА",
								"ХУСЕНОВНА"},
								"13.04.65",
								"",
								""))
		);
		

	}
	
	@Test
	public void parserTest() {
		for (Map.Entry<TerroristParser, List<Record>> pair : assertMap.entrySet()) {
			TerroristParser currentParser = pair.getKey();
			List<Record> expectedResult = pair.getValue();
			List<Record> result = currentParser.parseTerrorists();
			boolean resultsAreEqual = assertRecordListsEquals(result, expectedResult);
			Assert.assertTrue(resultsAreEqual);
		}
	}
	
	private boolean assertRecordListsEquals(List<Record> firstList, List<Record> secondList) {
		System.out.println(firstList);
		System.out.println(secondList);
		if (firstList == secondList)
			return true;
		if (firstList == null)
			return secondList == null;
		if (secondList == null)
			return firstList == null;
		if (firstList.size() != secondList.size())
			return false;
		
		for (int i = 0; i < firstList.size(); i++) {
			if (!firstList.get(i).equals(secondList.get(i)))
				return false;
		}
		return true;
	}
}
