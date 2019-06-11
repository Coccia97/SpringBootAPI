package com.esame.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esame.model.Record;
import com.esame.service.FilterService;
import com.esame.util.filter.Filter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonParser {

	
	public static ArrayList<Record> JsonParserColonna(Object filter) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException{
		
		ArrayList<Record> previousArray = new ArrayList<Record>();
		ArrayList<Record> filteredArray = new ArrayList<Record>();
		HashMap<String, Object> result = new ObjectMapper().convertValue(filter, HashMap.class);
		
		for(Map.Entry<String, Object> entry : result.entrySet()) {
			
			//ad ogni ciclo ripulisce l array "filteredArray"
			filteredArray = new ArrayList<Record>();
		    String column = entry.getKey();
		    Object filterParam = entry.getValue();
		    filteredArray = JsonParserOperator(column, filterParam, previousArray);
		    //ripulisce "previousArray" prima di riempirlo con "filteredArray"
		    previousArray = new ArrayList<Record>();
		    previousArray.addAll(filteredArray);
		}
		return filteredArray;		
	}
	
	
	public static ArrayList<Record> JsonParserOperator(String column, 
													   Object filterParam, 
												       ArrayList<Record> previousArray) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException{
		
		String type="";
		Filter filter;
		ArrayList<Record> filteredArray = new ArrayList<Record>();
		FilterService filtroService = new FilterService();
		HashMap<String, Object> result = new ObjectMapper().convertValue(filterParam, HashMap.class);
		
		for(Map.Entry<String, Object> entry : result.entrySet()) {
			
		    String operator = entry.getKey();
		    Object value = entry.getValue();
		    // Se operatore è type allora guarda se il valore (and o or)
		    // lancia il metodo runfilter corrispondente
		    if(operator.equals("type")) {
		    	type = (String) value;
		    	continue;
		    }
		    
		    filter = filtroService.instanceFilter(column, operator, value);
		    switch(type) {
		    
			    case "and":
			    	filteredArray = filtroService.runFilterAND(filter, previousArray);
			    	break;
			    case "or":
			    	filteredArray = filtroService.runFilterOR(filter, previousArray);
			    	break;
			    default:
			    	filteredArray = filtroService.runFilterOR(filter, previousArray);		    	
		    }
		}
		return filteredArray;	
	}
}