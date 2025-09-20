package com.klepek.datify;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "gemini.api.key=test-key"
})
class DatifyApplicationTests {

    @MockBean
    private EmbeddingModel embeddingModel;

    @Test
    void contextLoads() {
    }

}
