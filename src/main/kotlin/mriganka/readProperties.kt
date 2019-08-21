package presto.arch.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.web.client.RestTemplate
import propsview.arch.mapToProperties
import java.util.*


fun readLocalProperties(): Properties {
    val resource = ClassPathResource("boot.properties");
    val localProps = PropertiesLoaderUtils.loadProperties(resource)
    return localProps
}


fun readExternalProperties(uri: String): Properties {

    //loading resources from config server
    val restTemplate = RestTemplate()
    val response = restTemplate.getForEntity(uri, String::class.java)
    val mapper = ObjectMapper()
    val rawMap: Map<*, *> = mapper.readValue(response.body, object : TypeReference<Map<*, *>>() { })
    val lst = rawMap["propertySources"] as List<*>

    val mergedMap = mutableMapOf<Any?, Any?>();
    for (i in lst.lastIndex downTo 0) {
        val map = lst[i] as Map<*, *>
        val source = map.get("source") as Map<*, *>
        mergedMap.putAll(source)
    }

    val externalProps = mapToProperties(mergedMap)

    return externalProps
}
