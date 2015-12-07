package com.bastosbf.market.search.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.bastosbf.market.search.server.model.Market;
import com.bastosbf.market.search.server.model.MarketProduct;
import com.bastosbf.market.search.server.model.Product;

public class ProductDAO extends GenericDAO<Product> {

	public ProductDAO(SessionFactory factory) {
		super(factory);
	}

	public List<Product> list(int market) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(MarketProduct.class).add(
				Restrictions.eq("market.id", market));
		List<MarketProduct> list = criteria.list();
		List<Product> products = new ArrayList<Product>();
		for (MarketProduct mp : list) {
			Product product = mp.getProduct();
			products.add(product);
		}
		return products;
	}

	public Product get(String barcode) {
		Session session = factory.openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Product.class).add(
				Restrictions.eq("barcode", barcode));
		List<Product> list = criteria.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
