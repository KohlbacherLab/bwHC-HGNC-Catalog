package de.bwhc.catalogs.hgnc.impl



import de.bwhc.catalogs.hgnc._


import scala.concurrent.Future
import scala.io.Source



class HGNCCatalogProviderImpl extends HGNCCatalogProvider
{

  def getInstance: HGNCCatalog = {
    HGNCCatalogImpl
  }

}


object HGNCCatalogImpl extends HGNCCatalog
{

  private lazy val geneList: Iterable[HGNCGene] =
    Source.fromInputStream(
      this.getClass
       .getClassLoader
       .getResourceAsStream("hgnc.tsv")
    )
    .getLines
    .drop(1)  // Drop the TSV file header
    .map(_.split("\t"))
    .map(
      sn =>
        HGNCGene(
          HGNCGene.Symbol(sn(0)),
          sn(1)
        )
    )
    .toIterable


  def genes: Future[Iterable[HGNCGene]] = 
    Future.successful(geneList)


  def genesMatchingSymbol(
    sym: String
  ): Future[Iterable[HGNCGene]] =
    Future.successful(
      geneList.filter(_.symbol.value.contains(sym))
    )

  def genesMatchingName(
    name: String
  ): Future[Iterable[HGNCGene]] =
    Future.successful(
      geneList.filter(_.name.contains(name))
    )


  def geneWithSymbol(
    sym: HGNCGene.Symbol
  ): Future[Option[HGNCGene]] =
    Future.successful(
      geneList.find(_.symbol == sym)
    )


  def geneWithName(
    name: String
  ): Future[Option[HGNCGene]] =
    Future.successful(
      geneList.find(_.name == name)
    )

}
