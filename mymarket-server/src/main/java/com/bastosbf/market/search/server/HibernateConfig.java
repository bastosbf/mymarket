package com.bastosbf.market.search.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.bastosbf.market.search.server.model.Market;
import com.bastosbf.market.search.server.model.MarketProduct;
import com.bastosbf.market.search.server.model.MarketSuggestion;
import com.bastosbf.market.search.server.model.PriceSuggestion;
import com.bastosbf.market.search.server.model.Product;
import com.bastosbf.market.search.server.model.ProductSuggestion;
import com.bastosbf.market.search.server.operation.CollaborationRESTOperation;

public class HibernateConfig {

	public static final SessionFactory factory = new Configuration()
			.configure().addClass(Market.class).addClass(Product.class)
			.addClass(MarketProduct.class).addClass(MarketSuggestion.class)
			.addClass(ProductSuggestion.class).addClass(PriceSuggestion.class)
			.buildSessionFactory();

	public static void main(String[] args) throws Exception {
		CollaborationRESTOperation op = new CollaborationRESTOperation();
		op.suggestPrice(1, "000000", 10.53);
	}
}