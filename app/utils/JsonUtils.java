package utils;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JsonUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * {@link ObjectMapper} を取得します。
	 *
	 * @return
	 */
	public static final ObjectMapper mapper() {
		return MAPPER;
	}

	/**
	 * オブジェクトノードを新規作成して返却します。
	 *
	 * @return
	 */
	public static final ObjectNode object() {
		return JsonNodeFactory.instance.objectNode();
	}

	/**
	 * アレイノードを新規作成して返却します。
	 *
	 * @return
	 */
	public static final ArrayNode array() {
		return JsonNodeFactory.instance.arrayNode();
	}

	/**
	 * テキストノードを新規作成して返却します。
	 *
	 * @param text
	 * @return
	 */
	public static final TextNode text(String text) {
		return JsonNodeFactory.instance.textNode(text);
	}

	/**
	 * 指定のJSONノードをオブジェクトに変換します。 ノードとして指定できるのは、 JsonNode, String, byte[] の
	 * いずれかになります。 発生する例外は、 InvalidJsonException としてスローされます。
	 *
	 * @param node
	 * @param clazz
	 * @return
	 * @throws JsonProcessingException
	 */
	public static final <T> T toObject(Object node, Class<T> clazz) {
		try {
			if (node == null) {
				return null;
			}
			Class<?> nodeClass = node.getClass();
			if (nodeClass == String.class) {
				return MAPPER.readValue((String) node, clazz);
			} else if (nodeClass == byte[].class) {
				return MAPPER.readValue((byte[]) node, clazz);
			} else if (node instanceof ChannelBuffer) {
				return MAPPER.readValue(new ChannelBufferInputStream((ChannelBuffer) node), clazz);
			} else if (node instanceof JsonNode) {
				return MAPPER.treeToValue((JsonNode) node, clazz);
			} else {
				return MAPPER.convertValue(node, clazz);
			}
		} catch (JsonProcessingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

	/**
	 * 指定の {@link TypeReference} によるオブジェクト変換を行います。
	 *
	 * @param node
	 * @param typeReference
	 * @return
	 */
	public static final <T> T toObject(Object node, TypeReference<T> typeReference) {
		try {
			if (node == null) {
				return null;
			}
			Class<?> nodeClass = node.getClass();
			if (nodeClass == String.class) {
				return MAPPER.readValue((String) node, typeReference);
			} else if (nodeClass == byte[].class) {
				return MAPPER.readValue((byte[]) node, typeReference);
			} else if (node instanceof ChannelBuffer) {
				return MAPPER.readValue(new ChannelBufferInputStream((ChannelBuffer) node),
						typeReference);
			} else {
				return MAPPER.convertValue(node, typeReference);
			}
		} catch (JsonProcessingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

	/**
	 * JsonNodeを取得します。
	 *
	 * @param text
	 * @return
	 */
	public static final JsonNode toNode(String text) {
		try {
			return MAPPER.readTree(text);
		} catch (JsonProcessingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

	/**
	 * JsonNodeを取得します。
	 *
	 * @param buffer
	 * @return
	 */
	public static final JsonNode toNode(ChannelBuffer buffer) {
		try {
			return MAPPER.readTree(new ChannelBufferInputStream(buffer));
		} catch (JsonProcessingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

	/**
	 * JsonNodeを取得します。
	 *
	 * @param bytes
	 * @return
	 */
	public static final JsonNode toNode(byte[] bytes) {
		try {
			return MAPPER.readTree(bytes);
		} catch (JsonProcessingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

	/**
	 * JSON文字列を取得します。
	 *
	 * @param object
	 * @return
	 */
	public static final String toString(Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			throw new InvalidJsonException(e);
		} catch (JsonMappingException e) {
			throw new InvalidJsonException(e);
		} catch (IOException e) {
			throw new InvalidJsonException(e);
		}
	}

}
