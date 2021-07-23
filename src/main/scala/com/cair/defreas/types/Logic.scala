package com.cair.defreas.types

/** Typeclass of an AST type for a particular kind of logic, such as 
 *  propositional logic or defeasible ALC. Logics are parsed using a 
 *  _Syntax_, and hence may have many valid input formats. */
trait Logic[A] {
  val id: String
}
