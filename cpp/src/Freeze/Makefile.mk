# **********************************************************************
#
# Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
#
# **********************************************************************

$(project)_libraries = Freeze
$(project)_generated_includedir	:= $(project)/generated/Freeze

Freeze_targetdir        	:= $(libdir)
Freeze_cppflags      		:= -DFREEZE_API_EXPORTS
Freeze_sliceflags       	:= --include-dir Freeze --dll-export FREEZE_API
Freeze_dependencies		:= Ice
Freeze_libs             	:= db

Freeze_slice2freeze		:= Catalog CatalogIndexList

Freeze_Catalog			:= --dict Freeze::Catalog,string,Freeze::CatalogData
Freeze_Catalog_slice		:= $(slicedir)/Freeze/CatalogData.ice

Freeze_CatalogIndexList		:= --dict Freeze::CatalogIndexList,string,Ice::StringSeq
Freeze_CatalogIndexList_slice	:= $(ice_slicedir)/Ice/BuiltinSequences.ice

projects += $(project)
