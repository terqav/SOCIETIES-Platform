<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\EntityRepository;

/**
 * PerformanceTestResultRepository
 *
 * This class was generated by the Doctrine ORM. Add your own custom
 * repository methods below.
 */
class PerformanceTestResultRepository extends EntityRepository
{
	
	public function myFindAll($index, $limit)
	{
		$qb = $this->createQueryBuilder('result');
		
		$qb->setFirstResult($index)
			->setMaxResults($limit);
		
		return $qb->getQuery()
					->getResult();
	} 
}
