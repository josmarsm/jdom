/*--

 Copyright (C) 2000-2007 Jason Hunter & Brett McLaughlin.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.

 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.

 In addition, we request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many
 individuals on behalf of the JDOM Project and was originally
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.

 */

package org.jdom2.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.jdom2.Content;

/**
 * Partial implementation of {@link Filter}.
 *
 * @author Bradley S. Huffman
 * @param <T> The Generic type of content returned by this Filter
 */
abstract class AbstractFilter<T> implements Filter<T> {

	/**
	 * JDOM2 Serialization: Default mechanism
	 */
	private static final long serialVersionUID = 200L;

	@Override
	public final boolean matches(Object content) {
		return filter(content) != null;
	}

	@Override
	public List<T> filter(List<?> content) {
		if (content == null) {
			return Collections.emptyList();
		}
		if (content instanceof RandomAccess) {
			final int sz = content.size();
			final ArrayList<T> ret = new ArrayList<T>(sz);
			for (int i = 0; i < sz; i++) {
				final T c = filter(content.get(i));
				if (c != null) {
					ret.add(c);
				}
			}
			if (ret.isEmpty()) {
				return Collections.emptyList();
			}
			return Collections.unmodifiableList(ret);
		}
		final ArrayList<T> ret = new ArrayList<T>(10);
		for (Iterator<?> it = content.iterator(); it.hasNext(); ) {
			final T c = filter(it.next());
			if (c != null) {
				ret.add(c);
			}
		}
		if (ret.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(ret);
	}

	@Override
	public final Filter<?> negate() {
		if (this instanceof NegateFilter) {
			return ((NegateFilter)this).getBaseFilter();
		}
		return new NegateFilter(this);
	}

	@Override
	public final Filter<? extends Content> or(Filter<?> filter) {
		return new OrFilter(this, filter);
	}

	@Override
	public final Filter<?> and(Filter<?> filter) {
		return new AndFilter<Object>(this, filter);
	}

	@Override
	public <R> Filter<R> refine(Filter<R> filter) {
		return new RefineFilter<R>(this, filter);
	}
}
