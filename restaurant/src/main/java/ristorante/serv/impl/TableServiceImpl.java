package ristorante.serv.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import ristorante.entity.Tables;
import ristorante.repo.TablesRepository;
import ristorante.serv.TableService;

@Service
public class TableServiceImpl implements TableService {

	private final TablesRepository tableRepo;
	
	public TableServiceImpl(TablesRepository tableRepo) {
		this.tableRepo = tableRepo;
	}
	
	@Override
	public List<Tables> getAllTables() {
		return tableRepo.findAll();
	}

}
