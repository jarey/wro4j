/*
 * Copyright (c) 2008. All rights reserved.
 */
package ro.isdc.wro.util;

import java.util.Enumeration;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.http.HttpHeader;


/**
 * Utility class.
 *
 * @author Alex Objelean
 * @created Created on Nov 13, 2008
 */
public final class WroUtil {
  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(WroUtil.class);
  /**
   * Empty line pattern.
   */
  public static Pattern EMTPY_LINE_PATTERN = Pattern.compile("^[\\t ]*$\\r?\\n", Pattern.MULTILINE);
  /**
   * Thread safe date format used to transform milliseconds into date as string to put in response header.
   */
  private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("E, dd MMM yyyy HH:mm:ss z",
    TimeZone.getTimeZone("GMT"));


  /**
   * Transforms milliseconds into date format for response header of this form: Sat, 10 Apr 2010 17:31:31 GMT.
   *
   * @param milliseconds to transform
   * @return string representation of the date.
   */
  public static String toDateAsString(final long milliseconds) {
    return DATE_FORMAT.format(milliseconds);
  }


  /**
   * Retrieve pathInfo from a given location.
   *
   * @param location where to search contextPath.
   * @return pathInfo value.
   */
  public static String getPathInfoFromLocation(final String location) {
    if (StringUtils.isEmpty(location)) {
      throw new IllegalArgumentException("Location cannot be empty string!");
    }
    final String noSlash = location.substring(1);
    final int nextSlash = noSlash.indexOf('/');
    if (nextSlash == -1) {
      return "";
    }
    final String pathInfo = noSlash.substring(nextSlash);
    return pathInfo;
  }


  /**
   * @return folder path for some uri - the part until the last / character. For instance if request uri is:
   *         /app/wro/all.css => /app/wro/
   */
  public static String getFolderOfUri(final String uri) {
    return FilenameUtils.getFullPath(uri);
  }


  /**
   * <p>
   * Case insensitive check if a String starts with a specified prefix.
   * </p>
   * <p>
   * <code>null</code>s are handled without exceptions. Two <code>null</code> references are considered to be equal. The
   * comparison is case insensitive.
   * </p>
   *
   * <pre>
   * StringUtils.startsWithIgnoreCase(null, null)      = true
   * StringUtils.startsWithIgnoreCase(null, "abcdef")  = false
   * StringUtils.startsWithIgnoreCase("abc", null)     = false
   * StringUtils.startsWithIgnoreCase("abc", "abcdef") = true
   * StringUtils.startsWithIgnoreCase("abc", "ABCDEF") = true
   * </pre>
   *
   * @see java.lang.String#startsWith(String)
   * @param str the String to check, may be null
   * @param prefix the prefix to find, may be null
   * @return <code>true</code> if the String starts with the prefix, case insensitive, or both <code>null</code>
   * @since 2.4
   */
  public static boolean startsWithIgnoreCase(final String str, final String prefix) {
    return startsWith(str, prefix, true);
  }

  public static String toPackageAsFolder(final Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("Class cannot be null!");
    }
    return clazz.getPackage().getName().replace('.', '/');
  }

  /**
   * <p>
   * Check if a String starts with a specified prefix (optionally case insensitive).
   * </p>
   *
   * @see java.lang.String#startsWith(String)
   * @param str the String to check, may be null
   * @param prefix the prefix to find, may be null
   * @param ignoreCase inidicates whether the compare should ignore case (case insensitive) or not.
   * @return <code>true</code> if the String starts with the prefix or both <code>null</code>
   */
  private static boolean startsWith(final String str, final String prefix, final boolean ignoreCase) {
    if (str == null || prefix == null) {
      return (str == null && prefix == null);
    }
    if (prefix.length() > str.length()) {
      return false;
    }
    return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
  }


  /**
   * Retrieve servletPath from a given location.
   *
   * @param location where to search the servletPath.
   * @return ServletPath string value.
   */
  public static String getServletPathFromLocation(final String location) {
    return location.replace(getPathInfoFromLocation(location), "");
  }


  /**
   * @param request {@link HttpServletRequest} object.
   * @return true if this request support gzip encoding.
   */
  public static boolean isGzipSupported(final HttpServletRequest request) {
    return headerContains(request, HttpHeader.ACCEPT_ENCODING.toString(), "gzip");
  }


  /**
   * Checks if request contains the header value with a given value.
   *
   * @param request to check
   * @param header name of the header to check
   * @param value of the header to check
   */
  @SuppressWarnings("unchecked")
  private static boolean headerContains(final HttpServletRequest request, final String header, final String value) {
    final Enumeration<String> accepted = request.getHeaders(header);
    if (accepted != null) {
      while (accepted.hasMoreElements()) {
        final String headerValue = accepted.nextElement();
        if (headerValue.indexOf(value) != -1) {
          return true;
        }
      }
    }
    return false;
  }
}
