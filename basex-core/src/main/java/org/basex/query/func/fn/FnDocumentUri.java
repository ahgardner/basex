package org.basex.query.func.fn;

import org.basex.data.*;
import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class FnDocumentUri extends ContextFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final ANode node = toEmptyNode(ctxArg(0, qc), qc);
    if(node == null || node.type != NodeType.DOC) return null;
    // return empty sequence for documents constructed via parse-xml
    final Data data = node.data();
    if(data != null && data.meta.name.isEmpty()) return null;

    final byte[] uri = node.baseURI();
    return uri.length == 0 ? null : Uri.uri(uri, false);
  }
}
