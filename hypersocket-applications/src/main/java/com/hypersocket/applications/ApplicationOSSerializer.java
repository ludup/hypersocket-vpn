package com.hypersocket.applications;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ApplicationOSSerializer extends JsonSerializer<ApplicationOS> {

		  @Override
		  public void serialize(ApplicationOS value, JsonGenerator generator,
		            SerializerProvider provider) throws IOException,
		            JsonProcessingException {

		    generator.writeStartObject();
		    generator.writeFieldName("id");
		    generator.writeNumber(value.getId());
		    generator.writeFieldName("name");
		    generator.writeString(value.name());
		    generator.writeFieldName("displayName");
		    generator.writeString(value.getDisplayName());
		    generator.writeFieldName("version");
		    generator.writeString(value.getVersion());
		    generator.writeFieldName("family");
		    generator.writeString(value.getFamily());
		    
		    generator.writeEndObject();
		  }
		}