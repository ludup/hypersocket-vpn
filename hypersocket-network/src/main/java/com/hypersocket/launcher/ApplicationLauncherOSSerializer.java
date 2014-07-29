package com.hypersocket.launcher;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ApplicationLauncherOSSerializer extends JsonSerializer<ApplicationLauncherOS> {

		  @Override
		  public void serialize(ApplicationLauncherOS value, JsonGenerator generator,
		            SerializerProvider provider) throws IOException,
		            JsonProcessingException {

		    generator.writeStartObject();
		    generator.writeFieldName("id");
		    generator.writeNumber(value.getId());
		    generator.writeFieldName("name");
		    generator.writeString(value.getName());
		    generator.writeFieldName("version");
		    generator.writeString(value.getVersion());
		    generator.writeFieldName("family");
		    generator.writeString(value.getFamily());
		    
		    generator.writeEndObject();
		  }
		}