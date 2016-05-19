package org.europepmc.filter;

trait IDResolver {
   def isValidID(domain: String, id: String): Boolean 
}
