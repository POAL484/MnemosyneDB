package org.mnemosynehermesteam.server.mhrl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationDel;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationGet;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationGetItem;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationHDel;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationHSet;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationIncAndDec;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationIsMember;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationPop;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationPush;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationSAdd;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationSRem;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationSet;
import org.mnemosynehermesteam.server.mhrl.operations.MHRLOperationSize;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class MHRL {
    
    Core core;
    
    Map<String, MHRLOperation> OPS = new HashMap<>();

    public MHRL() {
        this(Core.getCore());
    }

    public MHRL(Core core) {
        this.core = core;
        
        OPS.put("GET", new MHRLOperationGet());
        OPS.put("GETITEM", new MHRLOperationGetItem());
        OPS.put("ISMEMBER", new MHRLOperationIsMember());
        OPS.put("SIZE", new MHRLOperationSize());
        OPS.put("SET", new MHRLOperationSet());
        OPS.put("INC", MHRLOperationIncAndDec.createInc());
        OPS.put("DEC", MHRLOperationIncAndDec.createDec());
        OPS.put("LPUSH", MHRLOperationPush.createLPush());
        OPS.put("RPUSH", MHRLOperationPush.createRPush());
        OPS.put("LPOP", MHRLOperationPop.createLPop());
        OPS.put("RPOP", MHRLOperationPop.createRPop());
        OPS.put("SADD", new MHRLOperationSAdd());
        OPS.put("SREM", new MHRLOperationSRem());
        OPS.put("DEL", new MHRLOperationDel());
        OPS.put("HSET", new MHRLOperationHSet());
        OPS.put("HDEL", new MHRLOperationHDel());
    }

    public MHRLOperationResult makeMHRLRequest(String opName, UUID userId, MHRLRequestBody mhrlRequestBody) {
        MHRLOperation op = OPS.get(opName);
        if ( op == null ) return MHRLOperationResult.generateErr(-2, "unknown MHRL operation: %s".formatted(opName));
        MHRLSyntaxParseResult syntaxRes = op.syntaxParse(mhrlRequestBody);
        if ( !syntaxRes.status() ) {
            log.debug("Syntax error {} found in request {}", syntaxRes.errCode(), mhrlRequestBody.toString());
            return MHRLOperationResult.generateErr(syntaxRes.errCode(), syntaxRes.errDescription());
        }
        return op.makeManipulateWithObject(core, userId, mhrlRequestBody.key()).start(op, mhrlRequestBody);
    }

    public boolean containsOperation(String op) {
        return OPS.containsKey(op);
    }    
}
