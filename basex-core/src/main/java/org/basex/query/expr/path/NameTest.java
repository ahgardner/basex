package org.basex.query.expr.path;

import org.basex.data.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.util.*;

/**
 * Name test.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class NameTest extends Test {
  /** Local name (can be {@code null}). */
  public final byte[] local;
  /** Default element namespace (can be {@code null}). */
  private final byte[] defNS;

  /**
   * Empty constructor ('*').
   * @param attr attribute flag (element otherwise)
   */
  public NameTest(final boolean attr) {
    this(attr, Kind.WILDCARD, null, null);
  }

  /**
   * Constructor.
   * @param attr attribute flag (element otherwise)
   * @param kind kind of name test
   * @param name name (may be {@code null})
   * @param defNS default element namespace (may be {@code null})
   */
  public NameTest(final boolean attr, final Kind kind, final QNm name, final byte[] defNS) {
    super(attr ? NodeType.ATT : NodeType.ELM);
    this.kind = kind;
    this.name = name;
    this.defNS = defNS != null ? defNS : Token.EMPTY;
    local = name != null ? name.local() : null;
    one = kind == Kind.URI_NAME;
  }

  @Override
  public boolean optimize(final Value value) {
    // skip optimizations if context value has no data reference
    if(value == null) return true;
    final Data data = value.data();
    if(data == null) return true;

    // skip optimizations if more than one namespace is defined in the database
    final byte[] dataNS = data.nspaces.globalUri();
    if(dataNS == null) return true;

    // check if test may yield results
    boolean results = true;
    if(kind == Kind.URI_NAME && !name.hasURI()) {
      if(type == NodeType.ATT || Token.eq(dataNS, defNS)) {
        // namespace is irrelevant/identical: only check local name
        kind = Kind.NAME;
      } else {
        // element and db default namespaces are different: no results
        results = false;
      }
    }

    // check existence of element/attribute names
    if(results) results = kind != Kind.NAME ||
        (type == NodeType.ELM ? data.elemNames : data.attrNames).contains(local);

    return results;
  }

  @Override
  public Test copy() {
    return this;
  }

  @Override
  public boolean eq(final ANode node) {
    // only elements and attributes will yield results
    if(node.type != type) return false;

    switch(kind) {
      // wildcard: accept all nodes
      case WILDCARD: return true;
      // namespaces wildcard: only check local name
      case NAME: return Token.eq(local, Token.local(node.name()));
      // name wildcard: only check namespace
      case URI: return Token.eq(name.uri(), node.qname().uri());
      // check attributes, or check everything
      default: return type == NodeType.ATT && name.uri().length == 0 ?
        Token.eq(local, node.name()) : name.eq(node.qname());
    }
  }

  /**
   * Checks if the specified name matches the test.
   * @param nm name
   * @return result of check
   */
  public boolean eq(final QNm nm) {
    switch(kind) {
      // wildcard: accept all nodes
      case WILDCARD: return true;
      // namespaces wildcard: only check local name
      case NAME: return Token.eq(local, nm.local());
      // name wildcard: only check namespace
      case URI: return Token.eq(name.uri(), nm.uri());
      // check everything
      default: return name.eq(nm);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if(!(obj instanceof NameTest)) return false;
    final NameTest nt = (NameTest) obj;
    if(kind != nt.kind) return false;
    switch(kind) {
      // wildcard: accept all nodes
      case WILDCARD: return true;
      // namespaces wildcard: only check local name
      case NAME: return Token.eq(local, nt.local);
      // name wildcard: only check namespace
      case URI: return Token.eq(name.uri(), nt.name.uri());
      // check everything
      default: return name.eq(nt.name);
    }
  }

  @Override
  public Test intersect(final Test other) {
    throw Util.notExpected(other);
  }

  @Override
  public String toString() {
    if(kind == Kind.WILDCARD) return "*";
    if(kind == Kind.NAME) return "*:" + Token.string(name.string());
    final String uri = name.uri().length == 0 || name.hasPrefix() ? "" :
      '{' + Token.string(name.uri()) + '}';
    return uri + (kind == Kind.URI ? "*" : Token.string(name.string()));
  }
}
