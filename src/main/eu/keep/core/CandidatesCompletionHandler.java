/*
 * * $Revision: 272 $ $Date: 2010-03-31 10:23:48 +0200 (Wed, 31 Mar 2010) $
 * * $Author: BKiers $
 */

/*
 * Copyright (c) 2002-2007, Marc Prud'hommeaux. All rights reserved.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package eu.keep.core;

import jline.CandidateListCompletionHandler;
import jline.CompletionHandler;
import jline.ConsoleReader;
import jline.CursorBuffer;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

// @author Marc Prud'hommeaux
class CandidatesCompletionHandler implements CompletionHandler {
    private static ResourceBundle loc           = ResourceBundle
                                                        .getBundle(CandidateListCompletionHandler.class
                                                                .getName());

    private boolean               eagerNewlines = false;

    public void setAlwaysIncludeNewline(boolean eagerNewlines) {
        this.eagerNewlines = eagerNewlines;
    }

    public boolean complete(final ConsoleReader reader, final List candidates, final int pos)
            throws IOException {
        CursorBuffer buf = reader.getCursorBuffer();

        // if there is only one completion, then fill in the buffer
        if (candidates.size() == 1) {
            String value = candidates.get(0).toString();

            // fail if the only candidate is the same as the current buffer
            if (value.equals(buf.toString())) {
                return false;
            }

            setBuffer(reader, value, pos);

            return true;
        }
        else if (candidates.size() > 1) {
            String value = getUnambiguousCompletions(candidates);
            setBuffer(reader, value, pos);
            // if(value.length() > 0) return true;
        }

        if (eagerNewlines)
            reader.printNewline();

        printCandidates(reader, candidates, eagerNewlines);

        // redraw the current console buffer
        reader.drawLine();

        return true;
    }

    public static void setBuffer(ConsoleReader reader, String value, int offset) throws IOException {
        while ((reader.getCursorBuffer().cursor > offset) && reader.backspace()) {
            ;
        }

        reader.putString(value);
        reader.setCursorPosition(offset + value.length());
    }

    @SuppressWarnings(value = "unchecked")
    public static final void printCandidates(ConsoleReader reader, Collection candidates,
            boolean eagerNewlines) throws IOException {
        Set distinct = new HashSet(candidates);

        // copy the values and make them distinct, without otherwise
        // affecting the ordering. Only do it if the sizes differ.
        if (distinct.size() != candidates.size()) {
            Collection copy = new ArrayList();

            for (Iterator i = candidates.iterator(); i.hasNext();) {
                Object next = i.next();

                if (!(copy.contains(next))) {
                    copy.add(next);
                }
            }

            candidates = copy;
        }

        reader.printNewline();
        reader.printColumns(candidates);
    }

    /**
     * Returns a root that matches all the {@link String} elements
     * of the specified {@link List}, or null if there are
     * no commalities. For example, if the list contains
     * <i>foobar</i>, <i>foobaz</i>, <i>foobuz</i>, the
     * method will return <i>foob</i>.
     */
    @SuppressWarnings(value = "unchecked")
    private final String getUnambiguousCompletions(final List candidates) {
        if ((candidates == null) || (candidates.size() == 0)) {
            return null;
        }

        // convert to an array for speed
        String[] strings = (String[]) candidates.toArray(new String[candidates.size()]);

        String first = strings[0];
        StringBuffer candidate = new StringBuffer();

        for (int i = 0; i < first.length(); i++) {
            if (startsWith(first.substring(0, i + 1), strings)) {
                candidate.append(first.charAt(i));
            }
            else {
                break;
            }
        }

        return candidate.toString();
    }

    /**
     * @return true is all the elements of <i>candidates</i>
     *         start with <i>starts</i>
     */
    private final boolean startsWith(final String starts, final String[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (!candidates[i].startsWith(starts)) {
                return false;
            }
        }

        return true;
    }
}
