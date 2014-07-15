package com.hypersocket.launcher;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class LauncherOSSerializer extends JsonSerializer<LauncherOS> {

		  @Override
		  public void serialize(LauncherOS value, JsonGenerator generator,
		            SerializerProvider provider) throws IOException,
		            JsonProcessingException {

		    generator.writeStartObject();
		    generator.writeFieldName("id");
		    generator.writeNumber(value.getId());
		    generator.writeFieldName("name");
		    generator.writeString(value.getName());
		    generator.writeFieldName("version");
		    generator.writeString(value.getVersion());
		    generator.writeEndObject();
		  }
		}