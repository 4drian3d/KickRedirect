package me.dreamerzero.kickredirect.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsTest {
    @ParameterizedTest
    @CsvSource({
            "Server Was Not online, server was nOt Online",
            "Kicked for hacking, Hacking",
            "Whitelisted Server, whitelist"
    })
    void containsIgnoreCaseTest(String first, String second) {
        assertTrue(Strings.containsIgnoreCase(first, second));
    }
}
