package com.namics.oss.spring.support.i18n;

import com.namics.oss.spring.support.i18n.dao.MessageSourceDao;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * NoCacheDaoMessageSourceTest.
 *
 * @author crfischer, Namics AG
 * @since 08.12.2017 14:52
 */
public class NoCacheDaoMessageSourceTest {

    private NoCacheDaoMessageSource candidate = new NoCacheDaoMessageSource();
    private MessageSourceDao messageSourceDaoMock = mock(MessageSourceDao.class);

    private String code = "message.source.1";
    private String lang = "fr";

    @Before
    public void init() {

        // assert default
        assertFalse(candidate.isAlwaysEscapeTicks());

        candidate.setAlwaysEscapeTicks(true);
        candidate.setMessageSourceDao(messageSourceDaoMock);
    }

    @Test
    public void citeNotEscapedSinceNoArgsNoPlaceholder() {

        String code = "message.source.1";
        String lang = "fr";
        String message = "'text' --> not escaped by default since no placeholder";
        String messageExpected = "'text' --> not escaped by default since no placeholder";

        when(messageSourceDaoMock.findByCodeAndLang(code, lang))
                .thenReturn(Arrays.asList(
                        new MessageResource().code(code)
                                .lang(lang)
                                .message(message)));


        String resolvedMessage = candidate.getMessage(code,null, new Locale(lang));

        assertEquals(messageExpected, resolvedMessage);
    }

    @Test
    public void citeEscapedSinceWithArgsWithPlaceholder() {

        String message = "'text' {0} --> escaped by default due to placeholder";
        String messageExpected = "'text' Arg1Value --> escaped by default due to placeholder";

        when(messageSourceDaoMock.findByCodeAndLang(code, lang))
                .thenReturn(Arrays.asList(
                        new MessageResource().code(code)
                                .lang(lang)
                                .message(message)));



        String resolvedMessage = candidate.getMessage(code,new Object[]{"Arg1Value"}, new Locale(lang));

        assertEquals(messageExpected, resolvedMessage);
    }

    @Test
    public void citeNotEscapedSinceWithArgsNoPlaceholder() {

        String message = "'text' --> not escaped by default since no placeholder present";
        String messageExpected = "'text' --> not escaped by default since no placeholder present";

        when(messageSourceDaoMock.findByCodeAndLang(code, lang))
                .thenReturn(Arrays.asList(
                        new MessageResource().code(code)
                                .lang(lang)
                                .message(message)));


        String resolvedMessage = candidate.getMessage(code,new Object[]{"Arg1Value"}, new Locale(lang));

        assertEquals(messageExpected, resolvedMessage);
    }

    @Test
    public void citeNotEscapedSinceWithArgsNoPlaceholderDoNotEscapeTicks() {

        candidate.setAlwaysEscapeTicks(false);

        String message = "'text' --> not escaped by default since no placeholder present";
        String messageExpected = "text --> not escaped by default since no placeholder present";

        when(messageSourceDaoMock.findByCodeAndLang(code, lang))
                .thenReturn(Arrays.asList(
                        new MessageResource().code(code)
                                .lang(lang)
                                .message(message)));


        String resolvedMessage = candidate.getMessage(code,new Object[]{"Arg1Value"}, new Locale(lang));

        assertEquals(messageExpected, resolvedMessage);
    }

    @Test
    public void citeEscapedSinceNoArgsWithPlaceholder() {

        String message = "'text' {0} --> escaped by default since placeholder present";
        String messageExpected = "''text'' {0} --> escaped by default since placeholder present";

        when(messageSourceDaoMock.findByCodeAndLang(code, lang))
                .thenReturn(Arrays.asList(
                        new MessageResource().code(code)
                                .lang(lang)
                                .message(message)));


        String resolvedMessage = candidate.getMessage(code,null, new Locale(lang));

        assertEquals(messageExpected, resolvedMessage);
    }
}
