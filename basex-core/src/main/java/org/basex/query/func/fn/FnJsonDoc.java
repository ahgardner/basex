package org.basex.query.func.fn;

import static org.basex.query.QueryError.*;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class FnJsonDoc extends FnParseJson {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final Item item;
    try {
      item = unparsedText(qc, false, false);
    } catch(final QueryException ex) {
      Util.debug(ex);
      throw ex.error() == QueryError.INVCHARS_X ?
        PARSE_JSON_X.get(info, ex.getLocalizedMessage()) : ex;
    }
    return item == null ? null : parse(item.string(info), false, qc);
  }
}
