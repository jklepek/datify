package com.klepek.datify;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
    "gemini.api.key=test-key"
})
class DatifyApplicationTests {

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @Test
    void contextLoads() {
    }

}
